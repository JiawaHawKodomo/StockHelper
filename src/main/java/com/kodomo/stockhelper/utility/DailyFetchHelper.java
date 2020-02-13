package com.kodomo.stockhelper.utility;

import com.kodomo.stockhelper.dao.*;
import com.kodomo.stockhelper.entity.*;
import com.kodomo.stockhelper.utility.httprequesthelper.DailyHttpRequestHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DailyFetchHelper {

    @Value("${stockhelper.initialDataLimit}")
    private Integer initialDataLimit;
    @Value("${stockhelper.maSegment}")
    private List<Integer> maSegment;
    @Value("${stockhelper.recommendedMinTurnOverRate}")
    private Double recommendedMinTurnOverRate;

    private final StockMaHelper stockMaHelper;
    private final StockInfoDao stockInfoDao;
    private final StockMaDao stockMaDao;
    private final StockTurnOverRateDao stockTurnOverRateDao;
    private final StockRecordDao stockRecordDao;
    private final DailyHttpRequestHelper dailyHttpRequestHelper;
    private final RecommendedStockDao recommendedStockDao;

    public DailyFetchHelper(StockInfoDao stockInfoDao, @Qualifier("tencentDailyHttpRequestHelper") DailyHttpRequestHelper dailyHttpRequestHelper, StockMaHelper stockMaHelper, StockMaDao stockMaDao, RecommendedStockDao recommendedStockDao, StockTurnOverRateDao stockTurnOverRateDao, StockRecordDao stockRecordDao) {
        this.stockInfoDao = stockInfoDao;
        this.dailyHttpRequestHelper = dailyHttpRequestHelper;
        this.stockMaHelper = stockMaHelper;
        this.stockMaDao = stockMaDao;
        this.recommendedStockDao = recommendedStockDao;
        this.stockTurnOverRateDao = stockTurnOverRateDao;
        this.stockRecordDao = stockRecordDao;
    }

    /**
     * 采集每日数据
     */
    @Transactional
    public void fetchDailyData() {
        //删除今日数据
        stockMaDao.deleteTodayData();
        stockRecordDao.deleteTodayData();
        stockTurnOverRateDao.deleteTodayData();

        log.info("开始爬取今天的数据...");
        List<StockInfo> stockInfos = stockInfoDao.findByLimit(initialDataLimit);

        int totalCount = 0;
        for (StockInfo stockInfo : stockInfos) {
            int count = dailyHttpRequestHelper.fetchDataAndSave(stockInfo.getStockId());
            totalCount += count;
        }
        log.info("爬取结束, 今天共爬取" + totalCount + "条数据");

        //计算今日的各ma值
        Date now = new Date();
        for (StockInfo stockInfo : stockInfos) {
            List<StockMa> stockMaList = new ArrayList<>(maSegment.size());
            for (Integer seg : maSegment) {
                StockMa stockMa = stockMaHelper.calculateMaBySegment(stockInfo.getStockId(), seg, now);
                stockMaList.add(stockMa);
            }
            //保存ma
            try {
                stockMaDao.saveAll(stockMaList);
            } catch (Exception ignored) {
            }
        }
    }


    /**
     * 计算推荐数据
     */
    @Transactional
    public void calculateRecommendedData() {
        //删除今日推荐数据
        recommendedStockDao.deleteTodayData();
        //生成推荐数据
        long now = new Date().getTime();
        Date today = new Date(now - (now % (1000L * 3600 * 24)));

        //筛选所有合格的
        List<Object[]> dataList = recommendedStockDao.filter(recommendedMinTurnOverRate);
        List<RecommendedStock> grouped = dataList.stream().map(a -> {
            RecommendedDTO recommendedDTO = new RecommendedDTO();
            recommendedDTO.setStockId((String) a[0]);
            recommendedDTO.setTurnOverRate((Double) a[1]);
            recommendedDTO.setDeltaMa((Double) a[2]);
            recommendedDTO.setMaSegment((Integer) a[3]);
            return recommendedDTO;
        }).collect(Collectors.groupingBy(RecommendedDTO::getStockId, Collectors.toList()))
                .values().stream()
                .filter(a -> a.size() == maSegment.size())
                .map(a -> {
                    RecommendedStock recommendedStock = new RecommendedStock();
                    StockInfo stockInfo = new StockInfo();
                    stockInfo.setStockId(a.get(0).getStockId());
                    recommendedStock.setStockInfo(stockInfo);
                    recommendedStock.setDate(today);
                    recommendedStock.setTurnOverRate(a.get(0).getTurnOverRate());
                    recommendedStock.setMa("{" + a.stream().map(b -> "'ma" + b.getMaSegment() + "':'" + String.format("%.2f", b.getDeltaMa()) + "'").reduce((c, d) -> c + "," + d).orElse("") + "}");
                    return recommendedStock;
                }).collect(Collectors.toList());

        recommendedStockDao.saveAll(grouped);
        log.info("共推荐" + grouped.size() + "个股票.");
    }
}

package com.kodomo.stockhelper.utility;

import com.kodomo.stockhelper.dao.*;
import com.kodomo.stockhelper.entity.*;
import com.kodomo.stockhelper.utility.httprequesthelper.DailyHttpRequestHelper;
import com.kodomo.stockhelper.utility.recommender.Recommender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class DailyFetchHelper {

    @Value("${stockhelper.initialDataLimit}")
    private Integer initialDataLimit;
    @Value("${stockhelper.maSegment}")
    private List<Integer> maSegment;
    @Value("${stockhelper.recommendedMinTurnOverRate}")
    private Double recommendedMinTurnOverRate;

    private final Recommender recommender;
    private final StockMaHelper stockMaHelper;
    private final StockInfoDao stockInfoDao;
    private final StockMaDao stockMaDao;
    private final StockTurnOverRateDao stockTurnOverRateDao;
    private final StockRecordDao stockRecordDao;
    private final DailyHttpRequestHelper dailyHttpRequestHelper;
    private final RecommendedStockDao recommendedStockDao;

    public DailyFetchHelper(StockInfoDao stockInfoDao, @Qualifier("tencentDailyHttpRequestHelper") DailyHttpRequestHelper dailyHttpRequestHelper, StockMaHelper stockMaHelper, StockMaDao stockMaDao, RecommendedStockDao recommendedStockDao, StockTurnOverRateDao stockTurnOverRateDao, StockRecordDao stockRecordDao, @Qualifier("recommender3") Recommender recommender) {
        this.stockInfoDao = stockInfoDao;
        this.dailyHttpRequestHelper = dailyHttpRequestHelper;
        this.stockMaHelper = stockMaHelper;
        this.stockMaDao = stockMaDao;
        this.recommendedStockDao = recommendedStockDao;
        this.stockTurnOverRateDao = stockTurnOverRateDao;
        this.stockRecordDao = stockRecordDao;
        this.recommender = recommender;
    }

    public void dailyTask(Date date){
        //爬取每日数据
        fetchDailyData(date);
        //计算结果
        calculateRecommendedData(date);
    }

    /**
     * 采集每日数据
     */
    @Transactional
    public void fetchDailyData(Date date) {
        //删除今日数据
        stockMaDao.deleteByDate(date);
        stockRecordDao.deleteByDate(date);
        stockTurnOverRateDao.deleteByDate(date);

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
    public void calculateRecommendedData(Date date) {
        //删除今日推荐数据
        recommendedStockDao.deleteByDate(date);
        //生成推荐数据
        recommender.calculateRecommendedData(date);
    }
}

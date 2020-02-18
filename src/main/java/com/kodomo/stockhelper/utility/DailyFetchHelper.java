package com.kodomo.stockhelper.utility;

import com.kodomo.stockhelper.dao.*;
import com.kodomo.stockhelper.entity.*;
import com.kodomo.stockhelper.utility.httprequesthelper.DailyHttpRequestHelper;
import com.kodomo.stockhelper.utility.recommender.Recommender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
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

    public DailyFetchHelper(StockInfoDao stockInfoDao, @Qualifier("tencentDailyHttpRequestHelper") DailyHttpRequestHelper dailyHttpRequestHelper, StockMaHelper stockMaHelper, StockMaDao stockMaDao, RecommendedStockDao recommendedStockDao, StockTurnOverRateDao stockTurnOverRateDao, StockRecordDao stockRecordDao, @Qualifier("recommender3Percentage") Recommender recommender) {
        this.stockInfoDao = stockInfoDao;
        this.dailyHttpRequestHelper = dailyHttpRequestHelper;
        this.stockMaHelper = stockMaHelper;
        this.stockMaDao = stockMaDao;
        this.recommendedStockDao = recommendedStockDao;
        this.stockTurnOverRateDao = stockTurnOverRateDao;
        this.stockRecordDao = stockRecordDao;
        this.recommender = recommender;
    }

    @Transactional
    public void dailyTask(Date date) {
        Date startTime = new Date();
        log.info("开始执行今日定时任务...");
        //爬取每日数据
        fetchDailyData(date);
        //计算结果
        calculateRecommendedData(date);
        log.info("定时任务执行完毕");
        Date endTime = new Date();
        log.info("Time: 共花费: " + (endTime.getTime() - startTime.getTime()) / 1000.0 + "s");
    }

    /**
     * 采集每日数据
     */
    @Transactional
    public void fetchDailyData(Date date) {
        //删除今日数据
        log.info("正在删除今天的冗余数据...");
        stockMaDao.deleteByDate(date);
        stockRecordDao.deleteByDate(date);
        stockTurnOverRateDao.deleteByDate(date);
        log.info("今天的冗余数据删除完毕");

        log.info("开始爬取今天的数据...");
        List<StockInfo> stockInfos = stockInfoDao.findByLimit(initialDataLimit);

        int totalCount = 0;
        for (StockInfo stockInfo : stockInfos) {
            int count = dailyHttpRequestHelper.fetchDataAndSave(stockInfo.getStockId());
            totalCount += count;
            if (totalCount % 100 == 0) {
                log.info("已爬取" + totalCount + "条数据...");
            }
        }
        log.info("爬取结束, 今天共爬取" + totalCount + "条数据");

        //计算今日的各ma值
        log.info("正在计算今天的MA值...");
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
                log.info("股票" + stockInfo.getStockId() + "的Ma保存完毕.");
            } catch (Exception e) {
                log.error("股票" + stockInfo.getStockId() + "的Ma保存出错.");
                log.error(e.getMessage());
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
        log.info("正在计算推荐数据...");
        recommender.calculateRecommendedData(date);
        log.info("推荐数据计算完成");
    }
}

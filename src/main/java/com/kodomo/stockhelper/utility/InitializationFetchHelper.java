package com.kodomo.stockhelper.utility;

import com.kodomo.stockhelper.dao.StockInfoDao;
import com.kodomo.stockhelper.dao.StockMaDao;
import com.kodomo.stockhelper.dao.StockRecordDao;
import com.kodomo.stockhelper.entity.StockInfo;
import com.kodomo.stockhelper.entity.StockMa;
import com.kodomo.stockhelper.entity.StockRecord;
import com.kodomo.stockhelper.utility.httprequesthelper.InitializationHttpRequestHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Shuaiyu Yao
 * @create 2020-02-12 23:13
 **/
@Slf4j
@Component
public class InitializationFetchHelper {

    @Value("${stockhelper.initialDataLimit}")
    private Integer initialDataLimit;
    @Value("${stockhelper.maSegment}")
    private List<Integer> maSegment;

    private final StockInfoDao stockInfoDao;
    private final StockRecordDao stockRecordDao;
    private final StockMaDao stockMaDao;

    private final InitializationHttpRequestHelper initializationHttpRequestHelper;
    private final StockMaHelper stockMaHelper;

    public InitializationFetchHelper(StockInfoDao stockInfoDao, @Qualifier("netEaseInitializationHttpRequestHelper") InitializationHttpRequestHelper initializationHttpRequestHelper, StockRecordDao stockRecordDao, StockMaHelper stockMaHelper, StockMaDao stockMaDao) {
        this.stockInfoDao = stockInfoDao;
        this.initializationHttpRequestHelper = initializationHttpRequestHelper;
        this.stockRecordDao = stockRecordDao;
        this.stockMaHelper = stockMaHelper;
        this.stockMaDao = stockMaDao;
    }

    /**
     * 从网上爬取初始数据， 时间较长
     */
    public void fetchInitialData() {
        log.info("开始从网上爬取历史数据, 需要花费较长时间...");
        log.info("共" + initialDataLimit + "个股票代码需要查询.");
        List<StockInfo> stockInfos = stockInfoDao.findByLimit(initialDataLimit);

        int totalCount = 0;
        int totalMaCount = 0;
        for (StockInfo stockInfo : stockInfos) {
            int count = fetchSingleStockInfo(stockInfo);
            log.info(stockInfo.getStockId() + "股票, 共保存" + count + "个数据.");
            totalCount += count;

            //计算ma
            int maCount = calculateMaForSingleStockInfo(stockInfo);
            log.info("共计算了" + maCount + "个MA数据");
            totalMaCount += maCount;
        }
        log.info("爬取历史数据结束, 共获取" + totalCount + "个数据.");
        log.info("共计算了" + totalMaCount + "个MA数据");
    }

    /**
     * 查询某个股票的历史数据
     *
     * @param stockInfo
     */
    private int fetchSingleStockInfo(StockInfo stockInfo) {
        String stockId = stockInfo.getStockId();
        if (stockId == null || stockId.equals("")) {
            return 0;
        }

        List<StockRecord> stockRecords = initializationHttpRequestHelper.fetchStockRecordById(stockId);
        stockRecordDao.saveAll(stockRecords);
        return stockRecords.size();
    }

    /**
     * 计算某个股票的ma
     *
     * @param stockInfo
     */
    private int calculateMaForSingleStockInfo(StockInfo stockInfo) {
        int maxSeg = maSegment.get(maSegment.size() - 1);
        Date now = new Date();
        Date earilest = new Date(now.getTime() - maxSeg * 2L * 1000 * 3600 * 60);
        List<StockRecord> stockRecords = stockRecordDao.findAfter(earilest, stockInfo.getStockId());

        List<StockMa> stockMaList = new ArrayList<>(maSegment.stream().reduce(Integer::sum).orElse(0) * 2);
        //按分段计算ma, 并存入
        for (Integer seg : maSegment) {
            for (int i = 0; i <= seg; i++) {
                Date date = new Date(now.getTime() - i * 1000L * 3600 * 24);
                StockMa stockMa = stockMaHelper.calculateMaBySegment(stockInfo.getStockId(), stockRecords, seg, date);
                stockMaList.add(stockMa);
            }
        }

        stockMaDao.saveAll(stockMaList);
        return stockMaList.size();
    }
}

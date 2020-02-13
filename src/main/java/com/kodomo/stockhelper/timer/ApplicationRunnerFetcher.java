package com.kodomo.stockhelper.timer;

import com.kodomo.stockhelper.dao.StockInfoDao;
import com.kodomo.stockhelper.dao.StockMaDao;
import com.kodomo.stockhelper.dao.StockRecordDao;
import com.kodomo.stockhelper.dao.StockTurnOverRateDao;
import com.kodomo.stockhelper.utility.FetchAllStockIdHelper;
import com.kodomo.stockhelper.utility.InitializationFetchHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ApplicationRunnerFetcher implements ApplicationRunner {

    private final InitializationFetchHelper initializationFetchHelper;
    private final FetchAllStockIdHelper fetchAllStockIdHelper;

    private final StockInfoDao stockInfoDao;
    private final StockRecordDao stockRecordDao;
    private final StockMaDao stockMaDao;
    private final StockTurnOverRateDao stockTurnOverRateDao;

    public ApplicationRunnerFetcher(FetchAllStockIdHelper fetchAllStockIdHelper, InitializationFetchHelper initializationFetchHelper, StockInfoDao stockInfoDao, StockRecordDao stockRecordDao, StockMaDao stockMaDao, StockTurnOverRateDao stockTurnOverRateDao) {
        this.fetchAllStockIdHelper = fetchAllStockIdHelper;
        this.initializationFetchHelper = initializationFetchHelper;
        this.stockInfoDao = stockInfoDao;
        this.stockRecordDao = stockRecordDao;
        this.stockMaDao = stockMaDao;
        this.stockTurnOverRateDao = stockTurnOverRateDao;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String[] sourceArgs = args.getSourceArgs();
        if (sourceArgs.length == 1) {
            if (sourceArgs[0].equals("init")) {
                log.info("初始化数据");
                //清空历史数据
                log.info("正在清空历史数据...");
                stockRecordDao.deleteAll();
                stockRecordDao.flush();
                stockMaDao.deleteAll();
                stockMaDao.flush();
                stockTurnOverRateDao.deleteAll();
                stockTurnOverRateDao.flush();
                stockInfoDao.deleteAll();
                stockInfoDao.flush();
                log.info("清空完毕.");
                log.info("获取全部A股基本信息");
                //获取全部A股信息
                fetchAllStockIdHelper.fetchAll();
                log.info("获取完毕");
                //爬取股票历史数据
                initializationFetchHelper.fetchInitialData();
            }
        }
    }
}

package com.kodomo.stockhelper.utility.httprequesthelper;

import com.kodomo.stockhelper.dao.StockRecordDao;
import com.kodomo.stockhelper.dao.StockTurnOverRateDao;
import com.kodomo.stockhelper.entity.StockInfo;
import com.kodomo.stockhelper.entity.StockRecord;
import com.kodomo.stockhelper.entity.StockTurnOverRate;
import com.kodomo.stockhelper.utility.StockIdFormattingHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author Shuaiyu Yao
 * @create 2020-02-13 14:23
 **/
@Slf4j
@Component
public class TencentDailyHttpRequestHelper implements DailyHttpRequestHelper {

    private final StockTurnOverRateDao stockTurnOverRateDao;
    private final StockRecordDao stockRecordDao;

    private final StockIdFormattingHelper stockIdFormattingHelper;

    public TencentDailyHttpRequestHelper(StockIdFormattingHelper stockIdFormattingHelper, StockRecordDao stockRecordDao, StockTurnOverRateDao stockTurnOverRateDao) {
        this.stockIdFormattingHelper = stockIdFormattingHelper;
        this.stockRecordDao = stockRecordDao;
        this.stockTurnOverRateDao = stockTurnOverRateDao;
    }

    @Override
    public int fetchDataAndSave(String stockId) {
        try {
            String formattedId = stockIdFormattingHelper.getAcronymPrefix(stockId);

            String url = "http://sqt.gtimg.cn/q=" + formattedId + "&offset=4,39";
            String response = RequestTools.sendGetRequest(url);

            //处理数据
            double stockValue = Double.valueOf(response.substring(response.indexOf("\"") + 1, response.indexOf("~")));
            double turnOverValue = Double.valueOf(response.substring(response.indexOf("~") + 1, response.lastIndexOf("\"")));

            //保存数据
            StockInfo stockInfo = new StockInfo();
            stockInfo.setStockId(stockId);
            saveStockRecord(stockInfo, stockValue);
            saveTurnOverRate(stockInfo, turnOverValue);

            return 2;
        } catch (Exception e) {
            log.error("股票id: " + stockId + "爬取不成功, 异常: ");
            log.error(e.getMessage());
            return 0;
        }
    }

    private void saveStockRecord(StockInfo stockInfo, double value) {
        try {
            StockRecord stockRecord = new StockRecord();
            stockRecord.setStockInfo(stockInfo);
            stockRecord.setValue(value);
            stockRecord.setDate(new Date());

            stockRecordDao.save(stockRecord);
        } catch (Exception ignored) {
        }
    }

    private void saveTurnOverRate(StockInfo stockInfo, double turnOverRate) {
        StockTurnOverRate stockTurnOverRate = new StockTurnOverRate();
        stockTurnOverRate.setStockInfo(stockInfo);
        stockTurnOverRate.setTurnOverRate(turnOverRate);
        stockTurnOverRate.setDate(new Date());

        stockTurnOverRateDao.save(stockTurnOverRate);
    }

}

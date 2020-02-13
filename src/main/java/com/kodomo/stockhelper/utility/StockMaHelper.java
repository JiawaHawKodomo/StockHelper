package com.kodomo.stockhelper.utility;

import com.kodomo.stockhelper.dao.StockMaDao;
import com.kodomo.stockhelper.dao.StockRecordDao;
import com.kodomo.stockhelper.entity.StockInfo;
import com.kodomo.stockhelper.entity.StockMa;
import com.kodomo.stockhelper.entity.StockRecord;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @author Shuaiyu Yao
 * @create 2020-02-13 13:10
 **/
@Component
public class StockMaHelper {

    private final StockRecordDao stockRecordDao;
    private final StockMaDao stockMaDao;

    public StockMaHelper(StockRecordDao stockRecordDao, StockMaDao stockMaDao) {
        this.stockRecordDao = stockRecordDao;
        this.stockMaDao = stockMaDao;
    }

    /**
     * 计算某天某个股票的某分段ma
     *
     * @param stockId
     * @param dataList
     * @param segment
     * @param date
     */
    public StockMa calculateMaBySegment(String stockId, List<StockRecord> dataList, int segment, Date date) {
        Date from = new Date(date.getTime() - segment * 1000L * 3600 * 24);
        Date to = new Date(date.getTime());

        int count = 0;
        double value = 0.0;
        for (StockRecord current : dataList) {
            if (current.getDate().before(from)) {
            } else if ((current.getDate().equals(from) || current.getDate().after(from))
                    && current.getDate().before(to)) {
                value += current.getValue();
                count++;
            } else {
                break;
            }
        }
        //计算平均值
        if (count != 0) {
            value /= count;
        }

        StockMa stockMa = new StockMa();
        StockInfo stockInfo = new StockInfo();
        stockInfo.setStockId(stockId);
        stockMa.setStockInfo(stockInfo);
        stockMa.setMaSegment(segment);
        stockMa.setDate(date);
        stockMa.setValue(value);

        return stockMa;
    }

    public StockMa calculateMaBySegment(String stockId, int segment, Date date) {
        Date earilest = new Date(new Date().getTime() - segment * 1000L * 3600 * 24);
        List<StockRecord> dataList = stockRecordDao.findAfter(earilest, stockId);

        return calculateMaBySegment(stockId, dataList, segment, date);
    }
}

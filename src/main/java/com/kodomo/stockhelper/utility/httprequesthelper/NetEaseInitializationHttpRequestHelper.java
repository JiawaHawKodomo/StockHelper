package com.kodomo.stockhelper.utility.httprequesthelper;

import com.kodomo.stockhelper.entity.StockInfo;
import com.kodomo.stockhelper.entity.StockRecord;
import com.kodomo.stockhelper.utility.StockIdFormattingHelper;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Shuaiyu Yao
 * @create 2020-02-13 10:24
 **/
@Component
public class NetEaseInitializationHttpRequestHelper implements InitializationHttpRequestHelper {

    private final StockIdFormattingHelper stockIdFormattingHelper;

    public NetEaseInitializationHttpRequestHelper(StockIdFormattingHelper stockIdFormattingHelper) {
        this.stockIdFormattingHelper = stockIdFormattingHelper;
    }

    @Override
    public List<StockRecord> fetchStockRecordById(String id) {
        //转换为数字前缀的id
        String formattedId = id;
        if (formattedId.length() == 6) {
            formattedId = stockIdFormattingHelper.getDigitalPrefix(id);
        }
        String url = "http://img1.money.126.net/data/hs/kline/day/times/" + formattedId + ".json";
        String response = RequestTools.sendGetRequest(url);

        Map<String, Object> map = new JacksonJsonParser().parseMap(response);
        List<Double> values = (List<Double>) map.get("closes");
        List<String> times = (List<String>) map.get("times");

        //二分寻找时间节点
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date now = new Date();
        Date days240before = new Date(now.getTime() - 240L * 1000 * 3600 * 24);
        int toFind = Integer.parseInt(dateFormat.format(days240before));

        int leftIndex = 0;
        int rightIndex = times.size() - 1;
        int targetIndex = 0;
        while (rightIndex - leftIndex > 1) {
            targetIndex = (leftIndex + rightIndex) / 2;
            int currentValue = Integer.parseInt(times.get(targetIndex));
            if (currentValue == toFind) {
                break;
            } else if (currentValue < toFind) {
                leftIndex = targetIndex;
            } else {
                rightIndex = targetIndex;
            }
        }

        //加入结果
        List<StockRecord> result = new ArrayList<>(240);
        for (int i = targetIndex; i < times.size(); i++) {
            StockRecord stockRecord = new StockRecord();
            StockInfo stockInfo = new StockInfo();
            stockInfo.setStockId(id);
            stockRecord.setValue(values.get(i));
            try {
                stockRecord.setDate(dateFormat.parse(times.get(i)));
            } catch (ParseException e) {
                e.printStackTrace();
                continue;
            }
            stockRecord.setStockInfo(stockInfo);
            result.add(stockRecord);
        }
        return result;
    }
}

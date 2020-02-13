package com.kodomo.stockhelper.utility.httprequesthelper;

import com.kodomo.stockhelper.entity.StockRecord;

import java.util.List;

/**
 * @author Shuaiyu Yao
 * @create 2020-02-13 10:23
 **/
public interface InitializationHttpRequestHelper {

    List<StockRecord> fetchStockRecordById(String id);

}

package com.kodomo.stockhelper.utility.httprequesthelper;

import com.kodomo.stockhelper.entity.StockRecord;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SohuInitializationHttpRequestHelper implements InitializationHttpRequestHelper {
    @Override
    public List<StockRecord> fetchStockRecordById(String id) {
        return null;
    }

}

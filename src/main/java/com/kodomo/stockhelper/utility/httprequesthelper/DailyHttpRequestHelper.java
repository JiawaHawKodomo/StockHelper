package com.kodomo.stockhelper.utility.httprequesthelper;

/**
 * @author Shuaiyu Yao
 * @create 2020-02-13 14:19
 **/
public interface DailyHttpRequestHelper {

    int fetchDataAndSave(String stockId);
}

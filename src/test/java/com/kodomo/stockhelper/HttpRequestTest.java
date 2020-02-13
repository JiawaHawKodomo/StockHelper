package com.kodomo.stockhelper;

import com.kodomo.stockhelper.entity.StockRecord;
import com.kodomo.stockhelper.utility.DailyFetchHelper;
import com.kodomo.stockhelper.utility.InitializationFetchHelper;
import com.kodomo.stockhelper.utility.httprequesthelper.NetEaseInitializationHttpRequestHelper;
import com.kodomo.stockhelper.utility.httprequesthelper.RequestTools;
import com.kodomo.stockhelper.utility.httprequesthelper.TencentDailyHttpRequestHelper;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.text.SimpleDateFormat;
import java.util.*;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = {StockhelperApplication.class})
public class HttpRequestTest {

    @Autowired
    protected NetEaseInitializationHttpRequestHelper netEaseHttpRequestHelper;
    @Autowired
    private InitializationFetchHelper initializationFetchHelper;
    @Autowired
    private TencentDailyHttpRequestHelper tencentDailyHttpRequestHelper;
    @Value("${stockhelper.maSegment}")
    private List<Integer> maSegment;
    @Autowired
    private DailyFetchHelper dailyFetchHelper;
    @Test
    @Ignore
    public void dateTest() {
        Date now = new Date();
        Date days240before = new Date(now.getTime() - 240L * 1000 * 3600 * 24);
        int toFind = Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(days240before));

        System.out.println(toFind);
    }

    @Test
    public void NetEaseInitialTest() {
        List<StockRecord> stockRecords = netEaseHttpRequestHelper.fetchStockRecordById("000022");
        System.out.println(stockRecords);
    }

    @Test
    @Ignore
    public void requestToolsTest() {
        String url = "http://img1.money.126.net/data/hs/kline/day/times/1399001.json";
        HttpHeaders httpHeaders = new HttpHeaders();
        String result = RequestTools.sendGetRequest(url, httpHeaders);
        System.out.println(result);
    }


    @Test
    public void fetchInitialDataTest() {
        initializationFetchHelper.fetchInitialData();
    }


    @Test
    public void getValueByListTest() {
        System.out.println(maSegment);
    }


    @Test
    public void daiayFetchTest() {
        dailyFetchHelper.fetchDailyData();
    }
}

package com.kodomo.stockhelper;

import com.kodomo.stockhelper.dao.RecommendedStockDao;
import com.kodomo.stockhelper.utility.DailyFetchHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = {StockhelperApplication.class})
public class RecommendedTest {

    @Autowired
    private RecommendedStockDao recommendedStockDao;
    @Autowired
    private DailyFetchHelper dailyFetchHelper;

    @Test
    public void filterTest() {
        List list = recommendedStockDao.filter(1.0);
        System.out.println(list);
    }

    @Test
    public void dailyTest() {
        //dailyFetchHelper.fetchDailyData();

        dailyFetchHelper.calculateRecommendedData();
    }
}

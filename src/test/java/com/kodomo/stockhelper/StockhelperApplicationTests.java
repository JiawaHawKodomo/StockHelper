package com.kodomo.stockhelper;

import com.kodomo.stockhelper.dao.RecommendedStockDao;
import com.kodomo.stockhelper.dao.StockInfoDao;
import com.kodomo.stockhelper.entity.RecommendedStock;
import com.kodomo.stockhelper.entity.StockInfo;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@WebAppConfiguration // 开启web应用配置
@SpringBootTest(classes = {StockhelperApplication.class})
public class StockhelperApplicationTests {

    @Autowired
    private StockInfoDao stockInfoDao;

    @Autowired
    private RecommendedStockDao recommendedStockDao;

    @Test
    public void test1() {
        StockInfo stockInfo = new StockInfo();
        stockInfo.setStockId("1111");
        stockInfo.setName("name");

        RecommendedStock recommendedStock = new RecommendedStock();
        recommendedStock.setStockInfo(stockInfo);
        recommendedStock.setDate(new Date());
        recommendedStock.setTurnOverRate(0.1);

        stockInfoDao.save(stockInfo);
        recommendedStockDao.save(recommendedStock);
    }

    @Test
    public void test2() {
        StockInfo s = new StockInfo();
        s.setStockId("1111");

        RecommendedStock rs = new RecommendedStock();
        rs.setStockInfo(s);

        List<RecommendedStock> list = recommendedStockDao.findAll(Example.of(rs));
        System.out.println(list);
    }

}

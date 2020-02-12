package com.kodomo.stockhelper;

import com.kodomo.stockhelper.utility.FetchAllStockIdException;
import com.kodomo.stockhelper.utility.FetchAllStockIdHelper;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = {StockhelperApplication.class})
public class FetchAllStockIdHelperTest {

    @Autowired
    private FetchAllStockIdHelper fetchAllStockIdHelper;

    @Test
    @Ignore
    public void fetchTest() {
        try {
            fetchAllStockIdHelper.fetchAll();
        } catch (FetchAllStockIdException e) {
            e.printStackTrace();
        }
    }
}

package com.kodomo.stockhelper.utility;

import org.springframework.stereotype.Component;

/**
 * @author Shuaiyu Yao
 * @create 2020-02-13 9:56
 **/
@Component
public class StockIdFormattingHelper {

    public String getAcronymPrefix(String id) {
        if (id.startsWith("0") || id.startsWith("3")) {
            return "sz" + id;
        } else {
            return "sh" + id;
        }
    }

    public String getDigitalPrefix(String id) {
        if (id.startsWith("0") || id.startsWith("3")) {
            return "1" + id;
        } else {
            return "0" + id;
        }
    }

}

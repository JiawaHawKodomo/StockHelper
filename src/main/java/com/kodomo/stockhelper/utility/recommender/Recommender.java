package com.kodomo.stockhelper.utility.recommender;

import java.util.Date;

/**
 * @author Shuaiyu Yao
 * @create 2020-02-13 20:10
 */
public interface Recommender {
    void calculateRecommendedData(Date date);
}

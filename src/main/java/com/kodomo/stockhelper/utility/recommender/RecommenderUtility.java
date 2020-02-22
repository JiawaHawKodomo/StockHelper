package com.kodomo.stockhelper.utility.recommender;

import com.kodomo.stockhelper.entity.RecommendedDTO;
import com.kodomo.stockhelper.entity.RecommendedStock;
import com.kodomo.stockhelper.entity.StockInfo;

import java.util.Date;
import java.util.List;

/**
 * @author Shuaiyu Yao
 * @create 2020-02-14 8:43
 **/
class RecommenderUtility {


    static RecommendedStock recommendedDTOToRecommendedStock(List<RecommendedDTO> a, Date date) {
        RecommendedStock recommendedStock = new RecommendedStock();
        StockInfo stockInfo = new StockInfo();
        stockInfo.setStockId(a.get(0).getStockId());
        recommendedStock.setStockInfo(stockInfo);
        recommendedStock.setDate(date);
        recommendedStock.setTurnOverRate(a.get(0).getTurnOverRate());
        recommendedStock.setMa("{" + a.stream().map(b -> "'ma" + b.getMaSegment() + "':'" + b.getDeltaMa() + "'").reduce((c, d) -> c + "," + d).orElse("") + "}");
        recommendedStock.setIsPercentage(false);
        return recommendedStock;
    }

    static RecommendedStock recommendedDTOToRecommendedStock(List<RecommendedDTO> a, Date date, boolean isPercentage) {
        RecommendedStock recommendedStock = recommendedDTOToRecommendedStock(a, date);
        recommendedStock.setIsPercentage(isPercentage);
        return recommendedStock;
    }
}

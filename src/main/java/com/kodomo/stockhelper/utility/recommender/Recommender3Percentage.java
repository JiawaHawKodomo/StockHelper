package com.kodomo.stockhelper.utility.recommender;

import com.kodomo.stockhelper.dao.RecommendedStockDao;
import com.kodomo.stockhelper.entity.RecommendedDTO;
import com.kodomo.stockhelper.entity.RecommendedStock;
import com.kodomo.stockhelper.entity.RecommendedTimesDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Shuaiyu Yao
 * @create 2020-02-13 20:12
 **/
@Slf4j
@Component
public class Recommender3Percentage implements Recommender {

    @Value("${stockhelper.initialDataLimit}")
    private Integer initialDataLimit;
    @Value("${stockhelper.maSegment}")
    private List<Integer> maSegment;
    @Value("${stockhelper.recommendedMinTurnOverRate}")
    private Double recommendedMinTurnOverRate;
    private final RecommendedStockDao recommendedStockDao;

    public Recommender3Percentage(RecommendedStockDao recommendedStockDao) {
        this.recommendedStockDao = recommendedStockDao;
    }

    @Override
    public void calculateRecommendedData(Date date) {
        //生成推荐数据
        Date day = new Date(date.getTime() - (date.getTime() % (1000L * 3600 * 24)));

        //昨天推荐的数据
        List<String> yesterdayRecommended = recommendedStockDao.getYesterdayRecommendId();
        //近30日的推荐数量map
        Map<String, Integer> near30daysRecommendedRecordMap =
                recommendedStockDao.getRecommendTimesNear30Days().stream()
                        .collect(Collectors.toMap(RecommendedTimesDTO::getStockId, RecommendedTimesDTO::getTimes));

        //筛选所有合格的
        List<RecommendedDTO> dataList = recommendedStockDao.filter2Percentage(recommendedMinTurnOverRate);
        List<RecommendedStock> grouped = dataList.stream()
                .filter(a -> {
                    if (a.getMaSegment() == null || a.getDeltaMa() == null) {
                        return false;
                    }
                    if (a.getMaSegment() == 5) return a.getDeltaMa() != 0.0;
                    else return a.getDeltaMa() > 0;
                })
                .collect(Collectors.groupingBy(RecommendedDTO::getStockId, Collectors.toList()))
                .values().stream()
                .filter(a -> a.size() == maSegment.size())
                .map(a -> {
                    RecommendedStock rs = RecommenderUtility.recommendedDTOToRecommendedStock(a, day, true);
                    //如果昨天推荐
                    String stockId = rs.getStockInfo().getStockId();
                    if (yesterdayRecommended.contains(stockId)) {
                        rs.setYesterdayRecommended(true);
                    }
                    //30日内推荐
                    if (near30daysRecommendedRecordMap.get(stockId) == null) {
                        rs.setNear30DaysRecommendTimes(1);
                    } else {
                        rs.setNear30DaysRecommendTimes(near30daysRecommendedRecordMap.get(stockId) + 1);
                    }
                    return rs;
                })
                .collect(Collectors.toList());


        recommendedStockDao.saveAll(grouped);
        log.info("共推荐" + grouped.size() + "个股票.");
    }
}

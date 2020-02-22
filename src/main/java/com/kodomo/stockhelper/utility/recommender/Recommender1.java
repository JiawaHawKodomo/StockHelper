package com.kodomo.stockhelper.utility.recommender;

import com.kodomo.stockhelper.dao.RecommendedStockDao;
import com.kodomo.stockhelper.entity.RecommendedDTO;
import com.kodomo.stockhelper.entity.RecommendedStock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Shuaiyu Yao
 * @create 2020-02-13 20:11
 **/
@Slf4j
@Component
public class Recommender1 implements Recommender {

    @Value("${stockhelper.initialDataLimit}")
    private Integer initialDataLimit;
    @Value("${stockhelper.maSegment}")
    private List<Integer> maSegment;
    @Value("${stockhelper.recommendedMinTurnOverRate}")
    private Double recommendedMinTurnOverRate;
    private final RecommendedStockDao recommendedStockDao;

    public Recommender1(RecommendedStockDao recommendedStockDao) {
        this.recommendedStockDao = recommendedStockDao;
    }

    @Override
    public void calculateRecommendedData(Date date) {
        //生成推荐数据
        Date day = new Date(date.getTime() - (date.getTime() % (1000L * 3600 * 24)));

        //筛选所有合格的
        List<RecommendedDTO> dataList = recommendedStockDao.filter(recommendedMinTurnOverRate);
        List<RecommendedStock> grouped = dataList.stream()
                .collect(Collectors.groupingBy(RecommendedDTO::getStockId, Collectors.toList()))
                .values().stream()
                .filter(a -> a.size() == maSegment.size())
                .map(a -> RecommenderUtility.recommendedDTOToRecommendedStock(a, day))
                .collect(Collectors.toList());

        recommendedStockDao.saveAll(grouped);
        log.info("共推荐" + grouped.size() + "个股票.");
    }
}

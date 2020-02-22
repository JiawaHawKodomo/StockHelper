package com.kodomo.stockhelper.dao;

import com.kodomo.stockhelper.entity.RecommendedDTO;
import com.kodomo.stockhelper.entity.RecommendedStock;
import com.kodomo.stockhelper.entity.RecommendedTimesDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Shuaiyu Yao
 * @create 2020-02-12 14:43
 **/
@Repository
public interface RecommendedStockDao extends JpaRepository<RecommendedStock, Integer> {

    static RecommendedTimesDTO objectArrayToRecommendedTimesDTO(Object[] a) {
        RecommendedTimesDTO recommendedTimesDTO = new RecommendedTimesDTO();
        recommendedTimesDTO.setStockId((String) a[0]);
        recommendedTimesDTO.setTimes(((BigInteger) a[1]).intValue());
        return recommendedTimesDTO;
    }

    static RecommendedDTO objectArrayToRecommendedDTO(Object[] a) {
        RecommendedDTO recommendedDTO = new RecommendedDTO();
        recommendedDTO.setStockId((String) a[0]);
        recommendedDTO.setTurnOverRate((Double) a[1]);
        recommendedDTO.setDeltaMa((Double) a[2]);
        recommendedDTO.setMaSegment((Integer) a[3]);
        return recommendedDTO;
    }

    default List<RecommendedDTO> filter(double minTurnOverRate) {
        return filterImpl(minTurnOverRate).stream()
                .map(RecommendedStockDao::objectArrayToRecommendedDTO)
                .collect(Collectors.toList());
    }

    @Deprecated
    @Query(nativeQuery = true,
            value = "SELECT sm.stock_id, tor.turn_over_rate, sm.value-sm2.value AS deltaMa, sm.ma_segment " +
                    "FROM stock_ma AS sm LEFT JOIN stock_ma AS sm2 ON sm2.stock_id=sm.stock_id LEFT JOIN stock_turn_over_rate AS tor ON tor.stock_id=sm.stock_id " +
                    "WHERE sm.ma_segment=sm2.ma_segment AND TO_DAYS(sm.date)=TO_DAYS(NOW()) AND TO_DAYS(sm2.date)=TO_DAYS(NOW())-sm.ma_segment AND TO_DAYS(tor.date)=TO_DAYS(NOW()) AND tor.turn_over_rate> ?1 " +
                    "AND sm.value>sm2.value")
    List<Object[]> filterImpl(double minTurnOverRate);

    default List<RecommendedDTO> filter2(double minTurnOverRate) {
        return filter2Impl(minTurnOverRate).stream()
                .map(RecommendedStockDao::objectArrayToRecommendedDTO)
                .collect(Collectors.toList());
    }

    @Deprecated
    @Query(nativeQuery = true,
            value = "SELECT sm.stock_id, tor.turn_over_rate, sm.value-sm2.value AS deltaMa, sm.ma_segment " +
                    "FROM stock_ma AS sm LEFT JOIN stock_ma AS sm2 ON sm2.stock_id=sm.stock_id LEFT JOIN stock_turn_over_rate AS tor ON tor.stock_id=sm.stock_id " +
                    "WHERE sm.ma_segment=sm2.ma_segment AND TO_DAYS(sm.date)=TO_DAYS(NOW()) AND TO_DAYS(sm2.date)=TO_DAYS(NOW())-sm.ma_segment AND TO_DAYS(tor.date)=TO_DAYS(NOW()) AND tor.turn_over_rate> ?1 ")
    List<Object[]> filter2Impl(double minTurnOverRate);


    default List<RecommendedDTO> filter2Percentage(double minTurnOverRate) {
        return filter2PercentageImpl(minTurnOverRate).stream()
                .map(RecommendedStockDao::objectArrayToRecommendedDTO)
                .collect(Collectors.toList());
    }

    @Deprecated
    @Query(nativeQuery = true,
            value = "SELECT sm.stock_id, tor.turn_over_rate, (sm.value-sm2.value)/sm2.value AS deltaMa, sm.ma_segment " +
                    "FROM stock_ma AS sm LEFT JOIN stock_ma AS sm2 ON sm2.stock_id=sm.stock_id LEFT JOIN stock_turn_over_rate AS tor ON tor.stock_id=sm.stock_id " +
                    "WHERE sm.ma_segment=sm2.ma_segment AND TO_DAYS(sm.date)=TO_DAYS(NOW()) AND TO_DAYS(sm2.date)=TO_DAYS(NOW())-sm.ma_segment AND TO_DAYS(tor.date)=TO_DAYS(NOW()) AND tor.turn_over_rate> ?1 ")
    List<Object[]> filter2PercentageImpl(double minTurnOverRate);


    default List<RecommendedTimesDTO> getRecommendTimesNear30Days() {
        return getRecommendTimesNear30DaysImpl().stream()
                .map(RecommendedStockDao::objectArrayToRecommendedTimesDTO)
                .collect(Collectors.toList());
    }

    @Deprecated
    @Query(nativeQuery = true,
            value = "SELECT stock_id, COUNT(*) FROM recommended_stock WHERE TO_DAYS(NOW())-TO_DAYS(DATE)<30 GROUP BY stock_id;")
    List<Object[]> getRecommendTimesNear30DaysImpl();


    @Query(nativeQuery = true,
            value = "SELECT stock_id FROM recommended_stock WHERE TO_DAYS(DATE)=TO_DAYS(NOW())-1")
    List<String> getYesterdayRecommendId();

    @Modifying
    @Query(value = "DELETE FROM recommended_stock WHERE TO_DAYS(DATE)=TO_DAYS(NOW());", nativeQuery = true)
    void deleteTodayData();

    @Modifying
    @Query(value = "delete from recommended_stock WHERE TO_DAYS(?1)=TO_DAYS(`date`)", nativeQuery = true)
    void deleteByDate(Date date);
}

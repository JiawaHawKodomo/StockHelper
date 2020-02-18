package com.kodomo.stockhelper.dao;

import com.kodomo.stockhelper.entity.RecommendedStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author Shuaiyu Yao
 * @create 2020-02-12 14:43
 **/
@Repository
public interface RecommendedStockDao extends JpaRepository<RecommendedStock, Integer> {

    @Query(nativeQuery = true,
            value = "SELECT sm.stock_id, tor.turn_over_rate, sm.value-sm2.value AS deltaMa, sm.ma_segment " +
                    "FROM stock_ma AS sm LEFT JOIN stock_ma AS sm2 ON sm2.stock_id=sm.stock_id LEFT JOIN stock_turn_over_rate AS tor ON tor.stock_id=sm.stock_id " +
                    "WHERE sm.ma_segment=sm2.ma_segment AND TO_DAYS(sm.date)=TO_DAYS(NOW()) AND TO_DAYS(sm2.date)=TO_DAYS(NOW())-sm.ma_segment AND TO_DAYS(tor.date)=TO_DAYS(NOW()) AND tor.turn_over_rate> ?1 " +
                    "AND sm.value>sm2.value")
    List<Object[]> filter(double minTurnOverRate);

    @Query(nativeQuery = true,
            value = "SELECT sm.stock_id, tor.turn_over_rate, sm.value-sm2.value AS deltaMa, sm.ma_segment " +
                    "FROM stock_ma AS sm LEFT JOIN stock_ma AS sm2 ON sm2.stock_id=sm.stock_id LEFT JOIN stock_turn_over_rate AS tor ON tor.stock_id=sm.stock_id " +
                    "WHERE sm.ma_segment=sm2.ma_segment AND TO_DAYS(sm.date)=TO_DAYS(NOW()) AND TO_DAYS(sm2.date)=TO_DAYS(NOW())-sm.ma_segment AND TO_DAYS(tor.date)=TO_DAYS(NOW()) AND tor.turn_over_rate> ?1 ")
    List<Object[]> filter2(double minTurnOverRate);

    @Query(nativeQuery = true,
            value = "SELECT sm.stock_id, tor.turn_over_rate, (sm.value-sm2.value)/sm2.value AS deltaMa, sm.ma_segment " +
                    "FROM stock_ma AS sm LEFT JOIN stock_ma AS sm2 ON sm2.stock_id=sm.stock_id LEFT JOIN stock_turn_over_rate AS tor ON tor.stock_id=sm.stock_id " +
                    "WHERE sm.ma_segment=sm2.ma_segment AND TO_DAYS(sm.date)=TO_DAYS(NOW()) AND TO_DAYS(sm2.date)=TO_DAYS(NOW())-sm.ma_segment AND TO_DAYS(tor.date)=TO_DAYS(NOW()) AND tor.turn_over_rate> ?1 ")
    List<Object[]> filter2Percentage(double minTurnOverRate);

    @Modifying
    @Query(value = "DELETE FROM recommended_stock WHERE TO_DAYS(DATE)=TO_DAYS(NOW());", nativeQuery = true)
    void deleteTodayData();

    @Modifying
    @Query(value = "delete from recommended_stock WHERE TO_DAYS(?1)=TO_DAYS(`date`)",nativeQuery = true)
    void deleteByDate(Date date);
}

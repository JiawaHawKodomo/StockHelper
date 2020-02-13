package com.kodomo.stockhelper.dao;

import com.kodomo.stockhelper.entity.StockTurnOverRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author Shuaiyu Yao
 * @create 2020-02-13 14:10
 **/
@Repository
public interface StockTurnOverRateDao extends JpaRepository<StockTurnOverRate, Integer> {

    @Query(value = "select * from stock_turn_over_rate where turn_over_rate >= ?1 and `date`=?2", nativeQuery = true)
    List<StockTurnOverRate> findByMinTurnOverRate(Double min, Date date);

    @Modifying
    @Query(value = "DELETE FROM stock_turn_over_rate WHERE TO_DAYS(DATE)=TO_DAYS(NOW());", nativeQuery = true)
    void deleteTodayData();
}

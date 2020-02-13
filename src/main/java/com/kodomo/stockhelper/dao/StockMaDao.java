package com.kodomo.stockhelper.dao;

import com.kodomo.stockhelper.entity.StockMa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Shuaiyu Yao
 * @create 2020-02-13 12:53
 **/
@Repository
public interface StockMaDao extends JpaRepository<StockMa, Integer> {

    @Modifying
    @Query(value = "DELETE FROM stock_ma WHERE TO_DAYS(DATE)=TO_DAYS(NOW());", nativeQuery = true)
    void deleteTodayData();
}

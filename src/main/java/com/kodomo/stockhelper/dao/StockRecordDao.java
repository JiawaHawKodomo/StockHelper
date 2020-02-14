package com.kodomo.stockhelper.dao;

import com.kodomo.stockhelper.entity.StockRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author Shuaiyu Yao
 * @create 2020-02-13 11:34
 **/
@Repository
public interface StockRecordDao extends JpaRepository<StockRecord, Integer> {

    @Query(nativeQuery = true, value = "select * from stock_record where `date` > ?1 and stock_id = ?2 order by `date` asc")
    List<StockRecord> findAfter(Date date, String stockId);

    @Modifying
    @Query(value = "DELETE FROM stock_record WHERE TO_DAYS(DATE)=TO_DAYS(NOW());", nativeQuery = true)
    void deleteTodayData();

    @Modifying
    @Query(value = "DELETE FROM stock_record WHERE TO_DAYS(DATE)=TO_DAYS(?1);", nativeQuery = true)
    void deleteByDate(Date date);
}

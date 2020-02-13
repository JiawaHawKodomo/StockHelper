package com.kodomo.stockhelper.dao;

import com.kodomo.stockhelper.entity.StockInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Shuaiyu Yao
 * @create 2020-02-12 14:42
 **/
@Repository
public interface StockInfoDao extends JpaRepository<StockInfo, String> {

    @Query(value = "select * from stock_info limit ?1", nativeQuery = true)
    List<StockInfo> findByLimit(Integer limit);
}

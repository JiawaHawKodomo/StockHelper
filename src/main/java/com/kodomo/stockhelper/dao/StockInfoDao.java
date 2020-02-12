package com.kodomo.stockhelper.dao;

import com.kodomo.stockhelper.entity.StockInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
/**
 * @author Shuaiyu Yao
 * @create 2020-02-12 14:42
 **/
@Repository
public interface StockInfoDao extends JpaRepository<StockInfo, String> {


}

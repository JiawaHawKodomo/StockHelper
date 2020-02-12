package com.kodomo.stockhelper.dao;

import com.kodomo.stockhelper.entity.RecommendedStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Shuaiyu Yao
 * @create 2020-02-12 14:43
 **/
@Repository
public interface RecommendedStockDao extends JpaRepository<RecommendedStock, Integer> {

}

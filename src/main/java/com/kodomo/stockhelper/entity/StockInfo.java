package com.kodomo.stockhelper.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Shuaiyu Yao
 * @create 2020-02-12 13:34
 **/
@Entity
@Data
@Table(name = "stock_info")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
public class StockInfo {

    @Id
    @Column(name = "stock_id")
    private String stockId;

    @Column(name = "name")
    private String name;

}

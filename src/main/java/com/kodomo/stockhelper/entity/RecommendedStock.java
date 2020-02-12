package com.kodomo.stockhelper.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Shuaiyu Yao
 * @create 2020-02-12 13:30
 **/
@Entity
@Getter
@Setter
@Table(name = "recommended_stock",
        indexes = {
                @Index(name = "time", columnList = "date")
        })
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
public class RecommendedStock {

    @Id
    @Column
    @GeneratedValue(generator = "generator_rst")
    @GenericGenerator(name = "generator_rst", strategy = "native")
    private Integer id;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id")
    private StockInfo stockInfo;

    @Column(name = "turn_over_rate")
    private Double turnOverRate;

    @Column
    private Double ma5;
    @Column
    private Double ma10;
    @Column
    private Double ma30;
    @Column
    private Double ma60;
    @Column
    private Double ma120;

    @Column(name = "date", updatable = false)
    @Temporal(TemporalType.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date date;
}

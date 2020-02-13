package com.kodomo.stockhelper.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Shuaiyu Yao
 * @create 2020-02-13 14:09
 **/
@Entity
@Data
@Table(name = "stock_turn_over_rate",
        indexes = {
                @Index(name = "tor_stockIdAndDate", columnList = "stock_id, date", unique = true)
        })
public class StockTurnOverRate {

    @Id
    @Column
    @GeneratedValue(generator = "generator_tor")
    @GenericGenerator(name = "generator_tor", strategy = "native")
    private Integer id;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id")
    private StockInfo stockInfo;

    @Column(name = "date", updatable = false)
    @Temporal(TemporalType.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date date;

    @Column(name = "turn_over_rate")
    private Double turnOverRate;

}

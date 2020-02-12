package com.kodomo.stockhelper.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Shuaiyu Yao
 * @create 2020-02-12 23:14
 **/
@Entity
@Getter
@Setter
@Table(name = "stock_record",
        indexes = {
                @Index(name = "record_stockIdAndDate", columnList = "stock_id, date", unique = true)
        })
public class StockRecord {

    @Id
    @Column
    @GeneratedValue(generator = "generator_sre")
    @GenericGenerator(name = "generator_sre", strategy = "native")
    private Integer id;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id")
    private StockInfo stockInfo;

    @Column(name = "date", updatable = false)
    @Temporal(TemporalType.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date date;

    @Column(name = "value")
    private Double value;
}

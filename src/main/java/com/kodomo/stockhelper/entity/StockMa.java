package com.kodomo.stockhelper.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Shuaiyu Yao
 * @create 2020-02-12 23:20
 **/
@Entity
@Getter
@Setter
@Table(name = "stock_ma",
        indexes = {
                @Index(name = "ma_stockIdAndDate", columnList = "stock_id, date, ma_segment", unique = true)
        })
public class StockMa {

    @Id
    @Column
    @GeneratedValue(generator = "generator_sma")
    @GenericGenerator(name = "generator_sma", strategy = "native")
    private Integer id;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id")
    private StockInfo stockInfo;

    @Column(name = "ma_segment")
    private Integer maSegment;

    @Column(name = "value")
    private Double value;

    @Column(name = "date", updatable = false)
    @Temporal(TemporalType.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date date;
}

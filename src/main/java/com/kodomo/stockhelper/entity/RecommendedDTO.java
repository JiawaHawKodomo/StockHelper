package com.kodomo.stockhelper.entity;

import lombok.*;

/**
 * @author Shuaiyu Yao
 * @create 2020-02-13 16:05
 **/
@Data
public class RecommendedDTO {


    private String stockId;

    private Double turnOverRate;

    private Integer maSegment;

    private Double deltaMa;
}

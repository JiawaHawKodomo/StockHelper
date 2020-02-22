package com.kodomo.stockhelper.entity;

import lombok.Data;

/**
 * @author Shuaiyu Yao
 * @create 2020-02-22 11:26
 **/
@Data
public class RecommendedTimesDTO {

    private String stockId;

    /**
     * 推荐次数
     */
    private Integer times;
}

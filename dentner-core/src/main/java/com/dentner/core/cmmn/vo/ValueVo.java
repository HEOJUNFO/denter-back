package com.dentner.core.cmmn.vo;

import lombok.Data;

@Data
public class ValueVo {
    private Integer valueNo;
    private String valueCn;
    private Integer memberNo;
    private String registerDt;
    private Double cementGapValue;
    private Double extraGapValue;
    private Double occlusalDistanceValue;
    private Double approximalDistanceValue;
    private Double heightMinimalValue;
}
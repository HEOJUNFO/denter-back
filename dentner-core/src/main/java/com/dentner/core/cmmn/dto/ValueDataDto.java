package com.dentner.core.cmmn.dto;

import lombok.Data;

@Data
public class ValueDataDto extends CommonDto{
    private Integer valueNo;
    private Double cementGapValue;
    private Double extraGapValue;
    private Double occlusalDistanceValue;
    private Double approximalDistanceValue;
    private Double heightMinimalValue;

}

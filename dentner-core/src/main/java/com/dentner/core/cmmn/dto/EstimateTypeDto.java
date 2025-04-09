package com.dentner.core.cmmn.dto;

import lombok.Data;

@Data
public class EstimateTypeDto {
    private Integer requestEstimateNo;
    private String typeName;
    private String typeUnit;
    private Integer typeAmount;
    private Integer typeCnt;
    private Integer registerNo;


}
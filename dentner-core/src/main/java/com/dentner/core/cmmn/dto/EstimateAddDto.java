package com.dentner.core.cmmn.dto;

import lombok.Data;

import java.util.List;

@Data
public class EstimateAddDto {
    private Integer requestEstimateNo;
    private Integer requestFormNo;
    private String estimateCn;
    private Integer estimateAmount;
    private String estimateDate;
    private String estimateTime;
    private Integer registerNo;

    private List<EstimateTypeDto> typeList;
}
package com.dentner.core.cmmn.dto;

import lombok.Data;

@Data
public class RequestEstimateDto extends CommonDto{
    private Integer requestFormNo;
    private Integer registerNo;
    private String estimateSe;
}

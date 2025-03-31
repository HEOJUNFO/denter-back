package com.dentner.core.cmmn.dto;

import lombok.Data;

@Data
public class RequestRemakingAddDto extends CommonDto{
    private Integer requestFormRemakingNo;
    private Integer requestFormNo;
    private String remakingSe;
    private String remakingDc;
    private Integer registerNo;
}

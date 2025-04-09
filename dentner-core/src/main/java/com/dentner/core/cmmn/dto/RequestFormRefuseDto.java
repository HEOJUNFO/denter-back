package com.dentner.core.cmmn.dto;

import lombok.Data;

@Data
public class RequestFormRefuseDto extends CommonDto{
    private Integer requestFormNo;
    private String requestRefuseCn;
    private Integer registerNo;
}

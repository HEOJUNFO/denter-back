package com.dentner.core.cmmn.dto;

import lombok.Data;

@Data
public class RequestFormContractDto extends CommonDto{
    private Integer requestFormNo;
    private String requestRefuseCn;
    private String requestContactSe;
    private Integer registerNo;
}

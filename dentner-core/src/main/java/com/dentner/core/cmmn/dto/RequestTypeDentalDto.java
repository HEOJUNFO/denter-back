package com.dentner.core.cmmn.dto;

import lombok.Data;

@Data
public class RequestTypeDentalDto extends CommonDto{
    private Integer requestTypeDentalNo;
    private Integer requestTypeNo;
    private String requestDentalSe;
    private String requestDentalValue;
    private Integer registerNo;
    private String error;
    private String calcMethod;

}

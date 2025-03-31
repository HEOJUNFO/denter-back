package com.dentner.core.cmmn.dto;

import lombok.Data;

@Data
public class RequestTypeDto extends CommonDto{
    private Integer requestTypeNo;
    private Integer requestDocNo;
    private String requestTypeValue;
    private String requestMiddleValue;
    private String requestTypeName;
    private int typeCount;
    private int typeAddCount;
    private Integer registerNo;
    private String error;
    private String direct;

}

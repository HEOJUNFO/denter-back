package com.dentner.core.cmmn.dto;

import lombok.Data;

import java.util.List;

@Data
public class RequestTypeDetailDto extends CommonDto{
    private Integer requestTypeNo;
    private Integer requestDocNo;
    private String requestTypeValue;
    private String requestMiddleValue;
    private String requestTypeName;
    private int typeCount;
    private int typeAddCount;
    private Integer registerNo;
    private String error;
    private String calcMethod;
    private String direct;
    private List<RequestTypeDentalDto> dentalList;

}

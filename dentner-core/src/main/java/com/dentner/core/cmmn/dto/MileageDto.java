package com.dentner.core.cmmn.dto;

import lombok.Data;

@Data
public class MileageDto extends CommonDto{
    private String amountFilter;
    private String statusFilter;
    private String fromDateFilter;
    private String toDateFilter;
    private Integer registerNo;
}
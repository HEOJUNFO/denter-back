package com.dentner.core.cmmn.dto;

import lombok.Data;

@Data
public class RequestFormDto extends CommonDto{
    private Integer requestFormNo;
    private String prostheticsFilter;
    private String statusFilter;
    private String myFilter;
    private String oldFilter;
    private String latestFilter;
    private String dedLineFilter;
    private String keyword;
    private String requestFormSe;
    private String fromDateFilter;
    private String toDateFilter;
    private String memberSe;
    private Integer registerNo;
}

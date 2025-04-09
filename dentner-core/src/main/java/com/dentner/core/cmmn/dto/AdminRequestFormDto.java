package com.dentner.core.cmmn.dto;

import lombok.Data;

@Data
public class AdminRequestFormDto extends CommonDto{
    private Integer requestFormNo;
    private String statusFilter;
    private String searchKeyword;
    private String requestFormSe;
    private String fromDateFilter;
    private String toDateFilter;
}

package com.dentner.core.cmmn.dto;

import lombok.Data;

@Data
public class AdminReportDto  extends CommonDto{
    private String searchKeyword;
    private String fromDateFilter;
    private String toDateFilter;
    private String statusFilter;
}
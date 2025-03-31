package com.dentner.core.cmmn.dto;

import lombok.Data;

@Data
public class AdminDocDto extends CommonDto{
    private String searchKeyword;
    private String searchType;
    private String fromDateFilter;
    private String toDateFilter;
    private String statusFilter;
    private String requestSe;
}
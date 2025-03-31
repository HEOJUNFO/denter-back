package com.dentner.core.cmmn.dto;

import lombok.Data;

import java.util.List;

@Data
public class DesignerDto extends CommonDto{
    private String prostheticsFilter;
    private String latestFilter;
    private String reviewFilter;
    private String ratingFilter;
    private String priceFilter;
    private String interestFilter;
    private String keyword;
    private Integer registerNo;
    private String randomSe;
    private String memberSe;
    private List<String> prostheticsFilterList;
}

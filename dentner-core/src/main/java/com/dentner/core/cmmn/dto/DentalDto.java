package com.dentner.core.cmmn.dto;

import lombok.Data;

@Data
public class DentalDto extends CommonDto{
    private Integer memberAreaFilter;
    private String fixProstheticsFilter;
    private String removableProstheticsFilter;
    private String correctFilter;
    private String allOnFilter;
    private String keyword;
    private Integer registerNo;
    private String interestFilter;
    private String latestFilter;
    private String memberSe;
    private String randomSe;
}

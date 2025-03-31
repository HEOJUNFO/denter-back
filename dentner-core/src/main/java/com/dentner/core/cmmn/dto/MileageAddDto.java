package com.dentner.core.cmmn.dto;

import lombok.Data;

@Data
public class MileageAddDto {
    private Integer mileageNo;
    private Integer cardNo;
    private Integer requestFormNo;
    private String mileageSe;
    private int mileageAmount;
    private String mileageCn;
    private String mileageUnit;
    private Integer registerNo;
    private String requestFormPayDc;
    private String addPaySe;
    private String orderNumber;

    // 추가금결제
    private String contents;
    private String toContents;
    
}
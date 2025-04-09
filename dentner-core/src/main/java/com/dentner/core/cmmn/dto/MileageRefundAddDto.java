package com.dentner.core.cmmn.dto;

import lombok.Data;

@Data
public class MileageRefundAddDto {
    private Integer mileageRefundNo;
    private Integer mileageNo;
    private Integer mileageRefundCodeNo;
    private String mileageRefundCn;
    private String mileageRefundSe;
    private String mileageStatus;
    private Integer registerNo;
}
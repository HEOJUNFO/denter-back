package com.dentner.core.cmmn.vo;

import lombok.Data;

@Data
public class MileageVo {
    private Integer mileageNo;
    private Integer cardNo;
    private String mileageSe;
    private String calculateSe;
    private int mileageAmount;
    private int payMileageAmount;
    private String mileageCn;
    private String mileageUnit;
    private String mileageStatus;
    private Integer registerNo;
    private String registerDt;
}
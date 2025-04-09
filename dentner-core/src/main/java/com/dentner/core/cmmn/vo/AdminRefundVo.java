package com.dentner.core.cmmn.vo;

import lombok.Data;

@Data
public class AdminRefundVo {
    private Integer mileageRefundNo;
    private Integer mileageNo;
    private String mileageUnit;
    private String mileageUnitName;
    private int mileageAmount;
    private String registerDt;
    private String mileageRefundConfirmDt;
    private String memberNickName;
    private String mileageRefundCodeNoName;
    private Integer registerNo;
    private String memberEmail;
    private String memberName;
    private String mileageRefundCn;
    private int mileageRefundCodeNo;
    
    private String amount;
}
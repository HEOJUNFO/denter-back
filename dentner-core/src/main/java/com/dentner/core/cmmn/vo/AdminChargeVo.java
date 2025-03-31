package com.dentner.core.cmmn.vo;

import lombok.Data;

@Data
public class AdminChargeVo {

    private Integer mileageNo;
    private String mileageUnit;
    private String mileageUnitName;

    private String mileageAmount;
    private String payType;
    private String registerDt;
    private String memberName;
    private String memberNickName;
    private String memberNo;
    private String memberEmail;
    private String refundYn;

    private String mileageRefundCn;
    private String refundDt;
    private String mileageStatus;
    private String mileageStatusName;
    private String mileageRefundCodeNoName;

}
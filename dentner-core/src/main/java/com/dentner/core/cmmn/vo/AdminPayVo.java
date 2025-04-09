package com.dentner.core.cmmn.vo;

import lombok.Data;

@Data
public class AdminPayVo {
    private Integer mileageNo;
    private String mileageUnit;
    private String mileageUnitName;
    private Integer mileageAmount;
    private String registerDt;
    private String memberEmail;
    private String memberName;
    private String memberNickName;
    private Integer memberNo;
    private String refundYn;
    private String mileageRefundCn;
    private String refundDt;
    private String mileageStatus;
    private String mileageSe;
    private Integer requestFormNo;
    private String requestFormSj;
    private String designerEmail;
    private String designerName;
    private String designerNickName;
    private Integer designerNo;
    private String calculateYn;
    private Integer calculateAmount;
    private Integer paymentAmount;
    private String requestDealStatus;
    private String requestStatusName;
    private String confirmAt;
    private String mileageCn;
    private String memberBankNoName;
    private String memberAccountName;
    private String memberAccountNumber;
    private String refundStatus;
    private String payType;
    private String cadYn;
}
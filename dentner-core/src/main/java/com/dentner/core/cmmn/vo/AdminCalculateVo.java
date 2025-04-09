package com.dentner.core.cmmn.vo;

import lombok.Data;

@Data
public class AdminCalculateVo {
    private Integer calculateNo;
    private Integer mileageNo;
    private Integer registerNo;
    private int calculateAmount;
    private int mileageAmount;
    private String registerDt;
    private String memberEmail;
    private String memberName;
    private String memberNickName;
    private String memberBankNoName;
    private int memberBankNo;
    private String memberAccountName;
    private String memberAccountNumber;
    private String billAt;
    private String confirmAt;
    private String mileageUnit;
}
package com.dentner.core.cmmn.vo;

import lombok.Data;

@Data
public class AdminDepositVo {
    private Integer mileageNo;
    private String mileageUnit;
    private String mileageUnitName;
    private int mileageAmount;
    private String requestFormSj;
    private String memberEmail;
    private String memberName;
    private String memberNickName;
    private String registerDt;
}
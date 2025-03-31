package com.dentner.core.cmmn.vo;

import lombok.Data;

import java.util.List;

@Data
public class CardVo {
    private Integer cardNo;
    private Integer cardCompanyNo;
    private String cardCompanyNoName;
    private String cardNumber;
    private String cardMonth;
    private String cardYear;
    private String cardPassword;
    private String idNum;
    private String idNumType;
    private String billKey;
    private String moId;
    private String buyerName;
    private String userId;

    private Integer registerNo;
}
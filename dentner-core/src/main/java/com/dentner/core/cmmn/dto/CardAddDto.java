package com.dentner.core.cmmn.dto;

import lombok.Data;

@Data
public class CardAddDto {
    private Integer cardNo;
    private Integer cardCompanyNo;
    private String cardNumber;
    private String cardCvc;
    private String cardMonth;
    private String cardYear;
    private String cardPassword;
    private String cardName;
    private String idNumType;
    private String idNum;
    private String billKey;
    private String moid;
    private String buyerName;
    private String userid;
    private Integer registerNo;
}
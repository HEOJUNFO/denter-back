package com.dentner.core.cmmn.dto;

import lombok.Data;

@Data
public class AuthCodeDto {
    private String authCode;
    private String tokenValue;
    private String phoneNo;
    private int memberContactNation;
}
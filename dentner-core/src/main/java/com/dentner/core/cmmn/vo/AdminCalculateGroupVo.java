package com.dentner.core.cmmn.vo;

import lombok.Data;

@Data
public class AdminCalculateGroupVo {
    private Integer calculateGroupNo;
    private int calculateGroupAmount;
    private String registerDt;
    private String confirmDt;
    private String confirmAt;
    private int confirmNo;
    private String confirmName;
    private String memberNickName;
}
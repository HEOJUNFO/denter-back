package com.dentner.core.cmmn.dto;

import lombok.Data;

@Data
public class AlarmTalkDto {
    private String templateCode;
    private String content;
    private String receiverNum;

    private String requestEmail;
    private String requestFormSj;
    private String requestFormSe;
    private String requestHp;
    private String designerHp;
    private String requestNickName;
    private String designerNickName;
    
    private String registerNo;
    private String designerNo;
    
    private String amount;

    private String msg;
    private String memberSe;
    private String memberTp;
    private String memberNo;
    private String memberNickName;
    private String requestFormNo;

}
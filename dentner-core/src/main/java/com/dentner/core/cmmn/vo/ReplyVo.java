package com.dentner.core.cmmn.vo;

import lombok.Data;

@Data
public class ReplyVo {
    private Integer requestFormAnswerNo;
    private Integer requestFormNo;
    private String answerCn;
    private Integer parentAnswerNo;
    private Integer registerNo;
    private String registerDt;
    private String memberNickName;
}
package com.dentner.core.cmmn.dto;

import lombok.Data;

@Data
public class ReplyAddDto {
    private Integer requestFormAnswerNo;
    private Integer requestFormNo;
    private String answerCn;
    private Integer parentAnswerNo;
    private Integer registerNo;
    private String url;
}

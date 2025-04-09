package com.dentner.core.cmmn.vo;

import lombok.Data;

@Data
public class AdminReportVo {
    private Integer reportNo;
    private Integer reporterNo;
    private Integer targetNo;
    private String blockStatus;
    private String reporterEmail;
    private String reporterName;
    private String reporterNickName;
    private String targetEmail;
    private String targetName;
    private String targetNickName;
    private int targetReportCount;
    private String reportCn;
    private String registerDt;
}
package com.dentner.core.cmmn.dto;

import lombok.Data;

@Data
public class ReportDto {
    private Integer targetNo;
    private Integer memberNo;
    private Integer reportCodeNo;
    private String reportCn;
    private String reportTp;
    private Integer reportTargetNo;
}
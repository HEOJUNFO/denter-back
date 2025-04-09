package com.dentner.core.cmmn.vo;

import lombok.Data;

import java.util.List;

@Data
public class AdminReportListVo {
    private List<AdminReportVo> list;
    private int cnt;
}
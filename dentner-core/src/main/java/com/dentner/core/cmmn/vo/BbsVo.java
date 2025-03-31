package com.dentner.core.cmmn.vo;

import lombok.Data;

import java.util.List;

@Data
public class BbsVo {
    private Integer bbsNo;
    private String bbsSj;
    private String bbsCn;
    private String bbsSe;
    private String bbsTp;
    private String bbsFixAt;
    private int bbsViews;
    private String registerDt;
}
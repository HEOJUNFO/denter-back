package com.dentner.core.cmmn.vo;

import lombok.Data;

@Data
public class TeethTypeVo {
    private Integer teethTypeNo;
    private String typeName;
    private int parentTypeNo;
    private int baseCnt;
    private String teethSe;
    private String calcMethod;
    private String typeEditYn;
}
package com.dentner.core.cmmn.vo;

import lombok.Data;

import java.util.List;

@Data
public class CodeVo {
    private int codeNo;
    private int codeParentNo;
    private String codeName;
    private String codeDesc;
    private String createdAt;
    private String deletedFg;
    private String codeEditYn;

    private List<CodeVo> detailList;
}
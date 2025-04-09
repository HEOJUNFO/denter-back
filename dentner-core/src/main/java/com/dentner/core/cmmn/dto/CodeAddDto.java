package com.dentner.core.cmmn.dto;

import lombok.Data;

import java.util.List;

@Data
public class CodeAddDto {
    private Integer codeNo;
    private Integer registerNo;
    private Integer codeParentNo;
    private String codeName;
    private String codeDesc;
    private String codeEditYn;

    private List<CodeAddDto> detailList;
}

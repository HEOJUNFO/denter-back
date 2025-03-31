package com.dentner.core.cmmn.vo;

import lombok.Data;

import java.util.List;

@Data
public class CodeListVo {
    private List<CodeVo> list;
    private int cnt;
}
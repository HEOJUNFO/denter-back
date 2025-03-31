package com.dentner.core.cmmn.vo;

import lombok.Data;

import java.util.List;

@Data
public class AdminRequestFormListVo {
    private List<AdminRequestFormVo> list;
    private int cnt;
}
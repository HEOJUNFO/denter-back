package com.dentner.core.cmmn.vo;

import lombok.Data;

import java.util.List;

@Data
public class AdminCalculateListVo {
    private List<AdminCalculateVo> list;
    private int cnt;
}
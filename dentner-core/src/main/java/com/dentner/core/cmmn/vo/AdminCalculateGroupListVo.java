package com.dentner.core.cmmn.vo;

import lombok.Data;

import java.util.List;

@Data
public class AdminCalculateGroupListVo {
    private List<AdminCalculateGroupVo> list;
    private int cnt;
}
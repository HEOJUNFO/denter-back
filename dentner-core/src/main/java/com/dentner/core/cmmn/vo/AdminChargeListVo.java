package com.dentner.core.cmmn.vo;

import lombok.Data;

import java.util.List;

@Data
public class AdminChargeListVo {
    private List<AdminChargeVo> list;
    private int cnt;
}
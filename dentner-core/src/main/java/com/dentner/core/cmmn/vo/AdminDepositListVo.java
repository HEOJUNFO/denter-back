package com.dentner.core.cmmn.vo;

import lombok.Data;

import java.util.List;

@Data
public class AdminDepositListVo {
    private List<AdminDepositVo> list;
    private int cnt;
}
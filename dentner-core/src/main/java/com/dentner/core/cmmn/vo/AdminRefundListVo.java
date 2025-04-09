package com.dentner.core.cmmn.vo;

import lombok.Data;

import java.util.List;

@Data
public class AdminRefundListVo {
    private List<AdminRefundVo> list;
    private int cnt;
}
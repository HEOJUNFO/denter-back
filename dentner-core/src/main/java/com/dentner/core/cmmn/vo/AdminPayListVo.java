package com.dentner.core.cmmn.vo;

import lombok.Data;

import java.util.List;

@Data
public class AdminPayListVo {
    private List<AdminPayVo> list;
    private int cnt;
}
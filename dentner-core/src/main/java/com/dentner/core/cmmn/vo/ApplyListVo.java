package com.dentner.core.cmmn.vo;

import lombok.Data;

import java.util.List;

@Data
public class ApplyListVo {
    private List<ApplyVo> list;
    private int cnt;
}
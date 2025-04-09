package com.dentner.core.cmmn.vo;

import lombok.Data;

import java.util.List;

@Data
public class BbsListVo {
    private List<BbsVo> list;
    private int cnt;
}
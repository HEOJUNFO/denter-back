package com.dentner.core.cmmn.vo;

import lombok.Data;

import java.util.List;

@Data
public class InterestListVo {
    private List<InterestVo> list;
    private int cnt;
}
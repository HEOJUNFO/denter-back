package com.dentner.core.cmmn.vo;

import lombok.Data;

import java.util.List;

@Data
public class RequestEstimateListVo {
    private List<RequestEstimateVo> list;
    private int cnt;
}
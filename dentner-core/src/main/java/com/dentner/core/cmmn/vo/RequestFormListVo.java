package com.dentner.core.cmmn.vo;

import lombok.Data;

import java.util.List;

@Data
public class RequestFormListVo {
    private List<RequestFormVo> list;
    private int cnt;
}
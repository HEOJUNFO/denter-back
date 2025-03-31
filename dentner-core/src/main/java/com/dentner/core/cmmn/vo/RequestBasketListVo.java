package com.dentner.core.cmmn.vo;

import lombok.Data;

import java.util.List;

@Data
public class RequestBasketListVo {
    private List<RequestBasketVo> list;
    private int cnt;
}
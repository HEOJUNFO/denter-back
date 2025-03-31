package com.dentner.core.cmmn.vo;

import lombok.Data;

import java.util.List;

@Data
public class ReviewListVo {
    private List<ReviewVo> list;
    private int cnt;
}
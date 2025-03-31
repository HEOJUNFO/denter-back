package com.dentner.core.cmmn.vo;

import lombok.Data;

import java.util.List;

@Data
public class DentalListVo {
    private List<DentalVo> list;
    private int cnt;
}
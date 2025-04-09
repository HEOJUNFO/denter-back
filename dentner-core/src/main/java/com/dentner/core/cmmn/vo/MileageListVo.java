package com.dentner.core.cmmn.vo;

import lombok.Data;

import java.util.List;

@Data
public class MileageListVo {
    private List<MileageVo> list;
    private int cnt;
}
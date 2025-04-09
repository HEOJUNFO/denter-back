package com.dentner.core.cmmn.vo;

import lombok.Data;

import java.util.List;

@Data
public class MileageDesignerCalculateListVo {
    private List<MileageDesignerCalculateVo> list;
    private int cnt;
}
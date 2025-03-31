package com.dentner.core.cmmn.vo;

import lombok.Data;

import java.util.List;

@Data
public class MileageDesignerListVo {
    private List<MileageDesignerVo> list;
    private int cnt;
}
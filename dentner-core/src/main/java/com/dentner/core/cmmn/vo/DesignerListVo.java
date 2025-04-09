package com.dentner.core.cmmn.vo;

import lombok.Data;

import java.util.List;

@Data
public class DesignerListVo {
    private List<DesignerVo> list;
    private int cnt;
}
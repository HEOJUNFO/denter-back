package com.dentner.core.cmmn.vo;

import lombok.Data;

import java.util.List;

@Data
public class BannerListVo {
    private List<BannerVo> list;
    private int cnt;
}
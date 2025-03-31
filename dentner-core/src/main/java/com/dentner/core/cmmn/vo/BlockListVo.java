package com.dentner.core.cmmn.vo;

import lombok.Data;

import java.util.List;

@Data
public class BlockListVo {
    private List<BlockVo> list;
    private int cnt;
}
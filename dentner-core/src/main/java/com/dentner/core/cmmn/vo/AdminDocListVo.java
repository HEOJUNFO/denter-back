package com.dentner.core.cmmn.vo;

import lombok.Data;

import java.util.List;

@Data
public class AdminDocListVo {
    private List<AdminDocVo> list;
    private int cnt;
}
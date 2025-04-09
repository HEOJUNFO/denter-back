package com.dentner.core.cmmn.vo;

import lombok.Data;

import java.util.List;

@Data
public class AlarmListVo {
    private List<AlarmVo> list;
    private int cnt;
}
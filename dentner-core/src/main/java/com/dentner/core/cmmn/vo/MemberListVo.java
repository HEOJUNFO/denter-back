package com.dentner.core.cmmn.vo;

import lombok.Data;

import java.util.List;

@Data
public class MemberListVo {
    private List<MemberVo> list;
    private int cnt;
}
package com.dentner.core.cmmn.vo;

import lombok.Data;

import java.util.List;

@Data
public class ThreeInfoVO {
    private Integer threeInfoNo;
    private Integer requestFormNo;
    private String threeSj;

    private List<ThreeFileVO> fileList;

}
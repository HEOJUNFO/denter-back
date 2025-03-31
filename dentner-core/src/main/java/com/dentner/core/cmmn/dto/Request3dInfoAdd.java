package com.dentner.core.cmmn.dto;

import lombok.Data;

@Data
public class Request3dInfoAdd extends CommonDto{
    private Integer threeInfoNo;
    private Integer requestFormNo;
    private String threeSj;
    private Integer registerNo;
}

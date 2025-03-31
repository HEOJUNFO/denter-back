package com.dentner.core.cmmn.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Request3dMemoAdd extends CommonDto{
    private Integer threeMemoNo;
    private Integer threeParentNo;
    private Integer threeFileNo;
    private String threeMemo;
    private BigDecimal threeMemoPosX;
    private BigDecimal threeMemoPosY;
    private BigDecimal threeMemoPosZ;
    private Integer registerNo;

    private String registerDt;
    private String registerName;
}

package com.dentner.core.cmmn.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ThreeMemoVO {
    private Integer threeMemoNo;
    private Integer threeParentNo;
    private Integer threeFileNo;
    private Integer registerNo;
    private String threeMemo;
    private BigDecimal threeMemoPosX;
    private BigDecimal threeMemoPosY;
    private BigDecimal threeMemoPosZ;

    private String registerDt;
    private String registerName;

}
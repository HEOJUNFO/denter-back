package com.dentner.core.cmmn.dto;

import lombok.Data;

@Data
public class TransactionCadAddDto extends CommonDto{
    private Integer requestFormPayNo;
    private Integer requestFormNo;
    private String requestPaySe;
    private String requestPayUnit;
    private Integer requestPayAmount;
    private String requestPayCn;
    private String fileDel;
    private String isRework;

    private Integer registerNo;

    private String docList;

}

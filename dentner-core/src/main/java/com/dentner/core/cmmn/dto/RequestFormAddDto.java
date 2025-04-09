package com.dentner.core.cmmn.dto;

import lombok.Data;

@Data
public class RequestFormAddDto extends CommonDto{
    private Integer requestFormNo;
    private String requestDocGroupsNo;
    private String requestFormSj;
    private String requestFormSe;
    private String requestFormType;
    private String requestExpireDate;
    private String requestExpireTime;
    private String requestDeadlineDate;
    private String requestDeadlineTime;
    private String requestSw;
    private String requestSwName;
    private String requestFormDc;
    private String requestStatus;
    private String requestDealStatus;
    private Integer registerNo;

    private Integer requestDesignerNo;

    private String requestFormJson;
}

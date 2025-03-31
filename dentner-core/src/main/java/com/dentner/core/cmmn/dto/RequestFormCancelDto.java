package com.dentner.core.cmmn.dto;

import lombok.Data;

@Data
public class RequestFormCancelDto extends CommonDto{
    private Integer requestFormNo;
    private Integer requestCancelNo;
    private String requestCancelEtcCn;
    private String requestStatus;
    private Integer registerNo;
    private String memberSe;
}

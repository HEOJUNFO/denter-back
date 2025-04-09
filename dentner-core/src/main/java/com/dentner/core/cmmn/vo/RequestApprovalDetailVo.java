package com.dentner.core.cmmn.vo;

import lombok.Data;

@Data
public class RequestApprovalDetailVo {
    private String requestProcessNoName;
    private Integer requestProcessNo;
    private String requestProcessEtcName;
    private String requestSe;
    private String requestBakNumber;
    private String requestNumber;
    private String requestDc;
    private String requestPonticSe;
    private String requestPonticSeName;
    private String implantType;
    private String valueSe;
    private String valueSj;
    private Integer cementGapValue;
    private Integer extraGapValue;
    private Integer occlusalDistanceValue;
    private Integer approximalDistanceValue;
    private Integer heightMinimalValue;

}
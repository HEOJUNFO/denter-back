package com.dentner.core.cmmn.dto;

import lombok.Data;

@Data
public class RequestDocListDto extends CommonDto{
    private Integer requestDocGroupNo;
    private Integer requestDocNo;

    private Integer requestProcessNo;
    private String requestProcessEtcName;
    private String requestDc;
    private String requestPonticSe;
    private String implantType;
    private String valueSe;
    private String valueSj;
    private Double cementGapValue;
    private Double extraGapValue;
    private Double occlusalDistanceValue;
    private Double approximalDistanceValue;
    private Double heightMinimalValue;

    private Integer registerNo;

    private RequestTypeDetailDto typeDetail;

    private String status;
    private String requestUuidKey;

    private String interestYn;
}

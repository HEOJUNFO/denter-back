package com.dentner.core.cmmn.dto;

import lombok.Data;

import java.util.List;

@Data
public class RequestDocDto extends CommonDto{
    private Integer requestDocGroupNo;
    private Integer requestDocNo;
    private String requestNumber;
    private String requestBakNumber;
    private String requestSe;
    private String requestJsonDc;
    private String saveAt;

    private Integer requestProcessNo;
    private String requestProcessEtcName;
    private String requestDc;
    private Integer registerNo;

    private RequestTypeDetailDto typeDetail;
    private String typeList;
    private String docList;
    private String fileDel;

    private String requestUuidKey;
}

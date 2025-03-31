package com.dentner.core.cmmn.vo;

import lombok.Data;

import java.util.List;

@Data
public class RequestApprovalVo {
    private String clientEmail;
    private String designerEmail;
    private String designerName;
    private String designerNo;
    private String memberBusinessName;
    private String estimateDt;
    private String requestDocDesc;
    private Integer requestDocGroupNo;
    private Integer requestFormNo;

    private List<RequestApprovalDetailVo> detailList;

}
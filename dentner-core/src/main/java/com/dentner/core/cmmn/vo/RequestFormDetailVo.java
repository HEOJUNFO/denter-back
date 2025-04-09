package com.dentner.core.cmmn.vo;

import lombok.Data;

import java.util.HashMap;
import java.util.List;

@Data
public class RequestFormDetailVo {
    private Integer requestFormNo;
    private String requestStatus;
    private String requestDealStatus;
    private String requestStatusName;
    private int requestDocCnt;
    private String requestFormSe;
    private String requestFormSj;
    private String registerDt;
    private String memberNickName;
    private String memberProfileImage;
    private String memberSw;
    private String swEtc;
    private String memberSwName;
    private String requestExpireDate;
    private String requestExpireTime;
    private String requestExpireTimeHour;
    private String requestExpireTimeMin;
    private String requestDeadlineDate;
    private String requestDeadlineTime;
    private String requestDeadlineTimeHour;
    private String requestDeadlineTimeMin;
    private String requestFormType;
    private String requestFormTypeName;
    private String requestFormDc;
    private String requestDocGroupsNo;
    private String payStatus;
    private Integer registerNo;
    private Integer requestDesignerNo;

    private List<HashMap<String, Object>> prostheticsList;
    private List<RequestBasketVo> requestList;
    private List<ReplyVo> replyList;

    private int targetAmount;
    private String designerName;

    private List<HashMap<String, Object>>  prosthetics;

}
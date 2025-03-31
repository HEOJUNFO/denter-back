package com.dentner.core.cmmn.vo;

import lombok.Data;

@Data
public class RequestFormVo {
    private Integer requestFormNo;
    private Integer memberNo;
    private Integer designerNo;
    private Integer requestDesignerNo;
    private Integer requestEstimateNo;
    private String requestDocGroupsNo;
    private String requestFormSj;
    private String requestFormSe;
    private String requestDealStatus;
    private String requestDealStatusName;
    private String requestStatus;
    private String requestStatusName;
    private int requestDocCnt;
    private String memberNickName;
    private String designerNickName;
    private String memberProfileImage;
    private String designerProfileImage;
    private String memberSw;
    private String memberSwName;
    private String requestExpireDate;
    private String requestExpireTime;
    private String requestExpireTimeHour;
    private String requestExpireTimeMin;
    private String requestDeadlineDate;
    private String requestDeadlineTime;
    private String requestDeadlineTimeHour;
    private String requestDeadlineTimeMin;
    private String registerDt;
    private String payStatus;
    private String addPayStatus;
    private String estimateReceiveYn;
    private String estimateSe;
    private String estimate3dYn;

    private Integer requestFormPayNo;
    private Integer requestRemakingNo;
    private String requestPayCn;
    private String requestPayDt;
    private String requestRemakingSeName;
    private String requestRemakingDt;
    private String requestRemakingDeletedAt;
    private String requestPayDeletedAt;
    private String reviewYn;
    private String cancelRequestYn;
    private String remakingAddYn;
    private String memberTp;
    private String memberOutAt;

    private int addAmount;
    private int totalAmount;
    private int estimateAmount;
    private int targetAmount;

    private RequestFormProstheticsVo prosthetics;
}
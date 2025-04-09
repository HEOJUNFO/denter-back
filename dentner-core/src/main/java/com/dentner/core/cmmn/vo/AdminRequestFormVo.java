package com.dentner.core.cmmn.vo;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class AdminRequestFormVo {
	private Integer rowNum;
    private Integer requestFormNo;
    private Integer memberNo;
    private String requestFormSj;
    private String requestFormSe;
    private String memberEmail;
    private String designerEmail;
    private String registerDt;
    private String requestStatus;
    private String requestStatusName;
    private String requestDealStatus;
    private String requestDealStatusName;
    private String requestNickName;
    private String designerNickName;
    private String mileageStatus;
    private int answerCnt;
    private int mileageAmount;
    private String requestExpireDate;
    private String requestExpireTime;
    private String estimateDate;
    private String estimateTime;
    private String requestDeadlineDate;
    private String requestDeadlineTime;

    private List<Map<String, Object>> docList;
    private List<ReplyVo> replyList;

    public String requestTypeName;
    private int count;

    private Integer requestEstimateNo;
}
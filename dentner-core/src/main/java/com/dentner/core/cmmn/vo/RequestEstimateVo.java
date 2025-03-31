package com.dentner.core.cmmn.vo;

import lombok.Data;

@Data
public class RequestEstimateVo {
    private Integer requestFormNo;
    private Integer requestEstimateNo;
    private Integer requestDesignerNo;
    private Integer memberNo;
    private String estimateCn;
    private Integer estimateAmount;
    private String estimateSe;
    private String memberProfileImage;
    private String designerProfileImage;
    private String memberDentistryName;
    private String memberNickName;
    private String oneIntroduction;
    private int reviewCnt;
    private double reviewAvg;
    private String prostheticsName;
    private String swNo;
    private String swEtc;
    private int wonPrice;
    private int dollarPrice;
    private String note;
    private String memberBusinessName;
    private String memberFirstValues;
    private String memberSwName;
    private String registerDt;
}
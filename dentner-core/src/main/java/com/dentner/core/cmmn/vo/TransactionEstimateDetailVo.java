package com.dentner.core.cmmn.vo;

import lombok.Data;

import java.util.HashMap;
import java.util.List;

@Data
public class TransactionEstimateDetailVo {
    private String memberDentistryName;
    private String oneIntroduction;
    private int reviewCnt;
    private double reviewAvg;
    private int modifyCnt;
    private int modifyWarrantyDay;
    private String swNo;
    private String swEtc;
    private int wonPrice;
    private int dollarPrice;
    private String memberSwName;
    private String estimateCn;
    private int estimateAmount;
    private Integer requestFormNo;
    private Integer requestEstimateNo;
    private Integer memberNo;
    private String designerProfileImage;
    private String requestFormSe;

    private String requestFormSj;
    private String requestStatus;
    private String requestExpireDate;
    private String requestExpireTimeHour;
    private String requestExpireTimeMin;
    private String requestSw;
    private String requestSwName;
    private String registerDt;
    private String estimateDate;
    private String estimateTimeHour;
    private String estimateTimeMin;
    private String estimateSe;

    private String requestDocDesc;
    private List<HashMap<String, Object>> prostheticsList;
    private List<S3FileVO> imageList;
    private List<S3FileListVO> fileList;
}
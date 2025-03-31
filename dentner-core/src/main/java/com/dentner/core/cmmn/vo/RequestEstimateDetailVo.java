package com.dentner.core.cmmn.vo;

import lombok.Data;

import java.util.HashMap;
import java.util.List;

@Data
public class RequestEstimateDetailVo {
    private String requestFormSe;
    private String requestDocDesc;
    private String requestFormSj;
    private String requestExpireDate;
    private String requestExpireTimeHour;
    private String requestExpireTimeMin;
    private String requestSw;
    private String requestSwName;
    private String registerDt;
    private List<HashMap<String, Object>> prostheticsList;
}
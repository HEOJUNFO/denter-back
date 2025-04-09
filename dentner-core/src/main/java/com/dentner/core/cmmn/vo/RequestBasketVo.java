package com.dentner.core.cmmn.vo;

import lombok.Data;

@Data
public class RequestBasketVo {
    private Integer requestDocGroupNo;
    private String requestDocName;
    private String requestSe;
    private String requestNumber;
    private String requestDocDesc;
    private String registerDt;
}
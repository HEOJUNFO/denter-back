package com.dentner.core.cmmn.vo;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class RequestAddPayVo {
    private Integer requestFormNo;
    private Integer requestFormPayNo;
    private String requestPaySe;
    private String requestPayUnit;
    private int requestPayAmount;
    private String requestPayCn;
    private String addPayYn;
    private String nickNm;
    //private String requestDocDesc;

    //private List<Map<String, Object>> docList;
    private Map<String, Object> form;
    private List<S3FileVO> fileList;

}
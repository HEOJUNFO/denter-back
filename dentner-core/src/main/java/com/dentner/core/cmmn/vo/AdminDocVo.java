package com.dentner.core.cmmn.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AdminDocVo {
    private Integer requestDocGroupNo;
    private Integer requestFormNo;
    private String requestNumber;
    private String requestFormSj;
    private String requestSe;
    private String requestFormSe;
    private String memberEmail;
    private String designerEmail;
    private String memberName;
    private String designerName;
    private String memberNickName;
    private String designerNickName;
    private String registerDt;
    private String docRequestDc;
    private String requestFormDc;
    
    private List<Map<String, Object>> prostheticsList;
    private List<Map<String, Object>> docList;
}
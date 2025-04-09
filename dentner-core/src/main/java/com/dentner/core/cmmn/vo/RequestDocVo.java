package com.dentner.core.cmmn.vo;

import lombok.Data;

import java.util.HashMap;
import java.util.List;

@Data
public class RequestDocVo {
    private Integer requestDocNo;
    private Integer registerNo;
    private String requestNumber;
    private int requestProcessNo;
    private String requestProcessEtcName;
    private String requestDc;
    private String requestProcessName;
    private String swNo;
    private String swEtc;
    private String swName;
    private String requestSe;
    private String saveAt;
    private String requestYn;

    private List<HashMap<String, Object>> prostheticsList;
    private List<S3FileVO> fileList;

}
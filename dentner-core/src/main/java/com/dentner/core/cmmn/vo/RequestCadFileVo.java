package com.dentner.core.cmmn.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class RequestCadFileVo {
    private Integer requestFormNo;
    private Integer requestDocGroupNo;
    private String requestNumber;
    private String requestDocDesc;
    private String requestDocName;
    private String registerDt;
    private String cadFile;
    private long cadFileSize;
    private Integer cadFileNo;
    private String cadRealName;
    private String addPayStatus;
    private String requestStatus;
    private String requestDealStatus;
}
package com.dentner.core.cmmn.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class RequestJsonVo {
    private Integer requestDocGroupNo;
    private String requestJsonDc;
    private List<Map<String, Object>> fileList;
    private List<Map<String, Object>> keyList;
}
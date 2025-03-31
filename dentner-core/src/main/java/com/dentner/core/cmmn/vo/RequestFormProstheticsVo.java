package com.dentner.core.cmmn.vo;

import lombok.Data;

import java.util.HashMap;
import java.util.List;

@Data
public class RequestFormProstheticsVo {
    private List<HashMap<String, Object>> requestList;
    private List<HashMap<String, Object>> prostheticsList;
}
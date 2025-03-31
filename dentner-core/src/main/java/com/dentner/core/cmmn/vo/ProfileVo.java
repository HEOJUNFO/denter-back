package com.dentner.core.cmmn.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ProfileVo {
    private Integer memberNo;
    private String memberProfileImage;
    private String memberNickName;
    private String swNo;
    private String memberSwName;
    private String swEtc;
    private String memberSe;
}
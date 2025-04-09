package com.dentner.core.cmmn.vo;

import lombok.Data;

import java.util.List;

@Data
public class ApplyVo {
    private int applyNo;
    private int createdNo;
    private String applySt;
    private String applyStName;
    private String teamName;
    private String entityStatus;
    private String entityStatusName;
    private String established;
    private String hqLocation;
    private String hqLocationName;
    private String representative;
    private String industry;
    private String industryName;
    private String businessItem;
    private String website;
    private String socialMediaLink;
    private String promoVideoLink;
    private String createdAt;
    private String surveyName;
    private String survey;

    private String year;
    private String month;
    private String date;

    private List<S3FileVO> formFile;
    private List<S3FileVO> irFile;

    private String irFileName;
    private String formFileName;
}
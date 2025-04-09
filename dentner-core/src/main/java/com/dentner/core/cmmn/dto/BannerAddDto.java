package com.dentner.core.cmmn.dto;

import lombok.Data;

@Data
public class BannerAddDto {
    private Integer bannerNo;
    private String bannerSe;
    private String bannerTitle;
    private String bannerDesc;
    private String bannerUrl;
    private int bannerOrdr;
    private int bannerViews;
    private String bannerImage;
    private String bannerFileName;
    private String bannerStartDt;
    private String bannerEndDt;
    private Integer bannerLine;

    private Integer registerNo;
    private String fileDel;
}

package com.dentner.core.cmmn.vo;

import lombok.Data;

import java.util.List;

@Data
public class BannerVo {
    private Integer bannerNo;
    private Integer fileNo;
    private String bannerSe;
    private String bannerDesc;
    private String bannerTitle;
    private String bannerStartDt;
    private String bannerEndDt;
    private String bannerUrl;
    private int bannerOrdr;
    private int bannerViews;
    private String bannerImage;
    private String bannerFileName;
    private String registerDt;
}
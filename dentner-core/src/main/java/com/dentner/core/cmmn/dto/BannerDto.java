package com.dentner.core.cmmn.dto;

import lombok.Data;

@Data
public class BannerDto extends CommonDto{
    private String searchKeyword;
    private String searchTp;
    private String bannerSe;
    private String bannerLine;
}

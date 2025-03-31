package com.dentner.core.cmmn.dto;

import lombok.Data;

@Data
public class ReviewDto extends CommonDto{
    private Integer reviewNo;
    private Integer targetNo;
    private Integer memberNo;
    private Integer requestFormNo;
    private Double reviewRate;
    private String reviewCn;
    private String fileDel;
    private String reviewSe;
    private String memberSe;
}
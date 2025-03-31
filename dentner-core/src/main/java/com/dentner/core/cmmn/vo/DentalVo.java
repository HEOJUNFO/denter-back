package com.dentner.core.cmmn.vo;

import lombok.Data;

import java.util.List;

@Data
public class DentalVo {
    private Integer memberNo;
    private String memberProfileImage;
    private String oneIntroduction;
    private String memberAreaName;
    private String memberDentistryName;
    private String fixProstheticsName;
    private String removableProstheticsName;
    private String correctName;
    private String allOnName;
    private String interestYn;
    private String aboutUs;
    private String establishYear;
    private String employeeCntName;
    private String memberAddress;
    private String memberDetailAddress;
    private String memberBusinessName;
    private String registerDt;
    private String memberSe;

    private List<S3FileVO> imageList;
}
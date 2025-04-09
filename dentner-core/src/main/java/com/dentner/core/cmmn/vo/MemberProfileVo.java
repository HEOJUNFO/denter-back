package com.dentner.core.cmmn.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
//@Alias("MemberVo") //Alias 지정가능
public class MemberProfileVo {

    private Integer memberProfileNo;
    private Integer memberNo;
    private String memberNickName;
    private String memberSe;
    private String oneIntroduction;
    private int memberAreaNo;
    private String fixProstheticsNo;
    private String removableProstheticsNo;
    private String correctNo;
    private String allOnNo;
    private String establishYear;
    private int employeeCnt;
    private String aboutUs;
    private String showAt;
    private int modifyCnt;
    private int modifyWarrantyDay;
    private String note;
    private String registerDt;
    private String updatedDt;
    private String deletedDt;
    private String deletedAt;
    private String memberProfileImage;
    private String swNo;
    private String swEtc;
    private String swNoName;
    private String swName;
    private String memberSwName;

    private String reviewAvg;
    private int reviewCnt;
    private int wonPrice;
    private int dollarPrice;

    private List<S3FileVO> imageList;
    private List<S3FileListVO> fileList;
    private List<MemberTypeVo> typeList;
}

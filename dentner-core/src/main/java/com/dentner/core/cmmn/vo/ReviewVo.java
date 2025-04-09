package com.dentner.core.cmmn.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
//@Alias("MemberVo") //Alias 지정가능
public class ReviewVo {

    private Integer reviewNo;
    private Integer targetNo;
    private Integer requestFormNo;
    private String registerDt;
    private String memberProfileImage;
    private String memberDentistryName;
    private String oneIntroduction;
    private String memberNickName;
    private String reviewCn;
    private double reviewRate;
    private Integer memberNo;

    private List<S3FileVO> fileList;

}

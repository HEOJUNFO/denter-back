package com.dentner.core.cmmn.vo;

import lombok.Data;

import java.util.List;

@Data
public class DesignerVo {
    private Integer memberNo;
    private String memberProfileImage;
    private String memberDentistryName;
    private String memberNickName;
    private String oneIntroduction;
    private int reviewCnt;
    private double reviewAvg;
    private String interestYn;
    private String prostheticsName;
    private String swNo;
    private String swNoName;
    private String swEtc;
    private int wonPrice;
    private int dollarPrice;
    private String note;
    private String memberBusinessName;
    private String registerDt;
    private String memberSe;

    private int modifyCnt;
    private int modifyDay;

    private List<S3FileVO> imageList;
    private List<MemberTypeVo> typeList;
    private List<S3FileListVO> fileList;
}
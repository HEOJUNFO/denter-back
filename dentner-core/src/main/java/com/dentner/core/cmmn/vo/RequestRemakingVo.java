package com.dentner.core.cmmn.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class RequestRemakingVo {
    private Integer requestFormRemakingNo;
    private Integer requestFormNo;
    private String remakingSe;
    private String remakingSeName;
    private String remakingDc;

    private List<S3FileVO> fileList;

}
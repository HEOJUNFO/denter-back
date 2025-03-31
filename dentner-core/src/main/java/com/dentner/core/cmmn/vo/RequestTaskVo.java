package com.dentner.core.cmmn.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//@Alias("MemberVo") //Alias 지정가능
public class RequestTaskVo {

    private Integer requestFormNo;
    private String requestExpireDate;
    private String requestExpireTime;
    private String requestFormSj;
    private String memberTp;
    private int estimateCnt;
}

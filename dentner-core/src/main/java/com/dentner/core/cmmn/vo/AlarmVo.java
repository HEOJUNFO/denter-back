package com.dentner.core.cmmn.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
//@Alias("MemberVo") //Alias 지정가능
public class AlarmVo {

    private Integer alarmNo;
    private String alarmSj;
    private String alarmCn;
    private String alarmSe;
    private String alarmUrl;
    private String readAt;
    private String registerDt;
    private Integer memberNo;

    private List<S3FileVO> fileList;

}

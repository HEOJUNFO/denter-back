package com.dentner.core.cmmn.dto;

import lombok.Data;

@Data
public class AlarmAddDto extends CommonDto{
    private String alarmSj;
    private String alarmCn;
    private String alarmSe;
    private String alarmUrl;
    private Integer memberNo;
}
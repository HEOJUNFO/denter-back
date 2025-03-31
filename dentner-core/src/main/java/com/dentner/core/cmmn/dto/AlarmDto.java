package com.dentner.core.cmmn.dto;

import lombok.Data;

@Data
public class AlarmDto extends CommonDto{
    private Integer memberNo;
    private String alarmSe;
    private String readAt;
}
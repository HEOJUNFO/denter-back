package com.dentner.core.cmmn.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlarmCodeVo {
    private Integer alarmCodeNo;
    private String alarmCodeName;
    private int memberCodeOrdr;
    private String alarmCodeSe;
    private String alarmCodeTp;
    private String alarmCodeDesc;
    private String alarmStatus;
}

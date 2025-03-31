package com.dentner.core.cmmn.dto;

import lombok.Data;

@Data
public class TermsAddDto extends CommonDto{
    private String termsCn;
    private String termsSe;
    private Integer registerNo;
}

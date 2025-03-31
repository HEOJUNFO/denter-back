package com.dentner.core.cmmn.dto;

import lombok.Data;

@Data
public class MemberDto extends CommonDto{
    private String searchKeyword;
    private String fromDateFilter;
    private String toDateFilter;
    private String memberSe;
    private String registerSe;
    private String memberTp;
    private String searchSe;
}

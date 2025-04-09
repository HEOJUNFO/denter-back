package com.dentner.core.cmmn.dto;

import lombok.Data;

@Data
public class PushDto extends CommonDto{
    private String token;
    private String title;
    private String body;
    private String url;
    private Integer memberNo;
}

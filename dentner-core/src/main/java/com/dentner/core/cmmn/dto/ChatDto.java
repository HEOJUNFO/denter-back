package com.dentner.core.cmmn.dto;

import lombok.Data;

@Data
public class ChatDto extends CommonDto{
    private Integer roomNo;
    private Integer toNo;
}
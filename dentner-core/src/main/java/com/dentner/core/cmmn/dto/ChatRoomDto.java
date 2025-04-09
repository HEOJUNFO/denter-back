package com.dentner.core.cmmn.dto;

import lombok.Data;

@Data
public class ChatRoomDto extends CommonDto{
    private Integer memberNo;
    private String targetSe;
    private String memberSe;

    private Integer targetNo;
}
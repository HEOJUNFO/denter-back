package com.dentner.core.cmmn.dto;

import lombok.Data;

@Data
public class ChatRoomAddDto {
    private Integer roomNo;
    private Integer memberNo;
    private Integer targetNo;   // 치자이너
    private Integer requestNo;  // 의뢰인
    private String memberSe;
    private String requestFormSe;
}
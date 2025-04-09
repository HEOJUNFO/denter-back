package com.dentner.core.cmmn.dto;

import lombok.Data;

@Data
public class ChatAddDto {
    private Integer chatNo;
    private Integer roomNo;
    private Integer fromNo;
    private Integer toNo;
    private String msg;
    private String msgType;
    private String memberSe;
    private String messageTime;
    private String readYn;
    private String fileUrl;
    private Integer fileSize;
    private String fileName;
    private String fileRealName;
    private String memberNickName;
}
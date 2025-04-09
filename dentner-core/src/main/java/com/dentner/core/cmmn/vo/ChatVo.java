package com.dentner.core.cmmn.vo;

import lombok.Data;

@Data
public class ChatVo {
    private Integer roomNo;
    private Integer chatNo;
    private String msg;
    private String memberNickName;
    private String msgType;
    private String messageDate;
    private String messageTime;
    private String postType;
    private String readYn;
    private String fileUrl;
    private Integer fileSize;
    private String fileName;
    private String fileRealName;
}
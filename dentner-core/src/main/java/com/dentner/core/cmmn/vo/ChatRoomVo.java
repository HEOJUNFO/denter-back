package com.dentner.core.cmmn.vo;

import lombok.Data;

import java.util.List;

@Data
public class ChatRoomVo {
    private Integer roomNo;
    private Integer targetNo;
    private String lastMessage;
    private String memberAreaName;
    private String memberNickName;
    private String memberProfileImage;
    private String dentalProfileImage;
    private String lastMessageTime;
    private String prostheticsName;
    private String swEtc;
    private String swName;
    private String interestYn;
    private String unreadMessageCount;
    private String memberOutAt;
}
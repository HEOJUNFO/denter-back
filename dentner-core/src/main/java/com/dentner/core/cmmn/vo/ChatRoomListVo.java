package com.dentner.core.cmmn.vo;

import lombok.Data;

import java.util.List;

@Data
public class ChatRoomListVo {
    private List<ChatRoomVo> list;
    private int cnt;
}
package com.dentner.core.cmmn.vo;

import lombok.Data;

import java.util.List;

@Data
public class ChatListVo {
    private List<ChatVo> list;
    private int cnt;
}
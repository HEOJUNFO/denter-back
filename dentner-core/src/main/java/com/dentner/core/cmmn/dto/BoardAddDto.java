package com.dentner.core.cmmn.dto;

import lombok.Data;

@Data
public class BoardAddDto {
    private int boardNo;
    private int customerNo;
    private int createdNo;
    private String boardTp;
    private String boardSubject;
    private String boardContent;

    private String fileDel;
}

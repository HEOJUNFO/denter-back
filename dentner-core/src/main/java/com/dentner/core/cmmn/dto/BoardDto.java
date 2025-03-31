package com.dentner.core.cmmn.dto;

import lombok.Data;

@Data
public class BoardDto extends CommonDto{
    private String searchKeyword;
    private String searchTp;
    private int customerNo;
    private String boardTp;
}

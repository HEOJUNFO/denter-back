package com.dentner.core.cmmn.dto;

import lombok.Data;

@Data
public class CodeDto extends CommonDto{
    private String searchKeyword;
    private String searchTp;
    private Integer codeParentNo;
}

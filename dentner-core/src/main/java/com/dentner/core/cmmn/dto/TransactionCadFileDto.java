package com.dentner.core.cmmn.dto;

import lombok.Data;

@Data
public class TransactionCadFileDto extends CommonDto{
    private Integer requestDocGroupNo;
    private String fileName;
    private String fileOrgName;
}

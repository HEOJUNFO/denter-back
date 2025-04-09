package com.dentner.core.cmmn.dto;

import lombok.Data;

@Data
public class ThreeFileDto extends CommonDto{
    private String fileTp;
    private String fileName;
    private String fileOrgName;
    private int fileOrdr;
}

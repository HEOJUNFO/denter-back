package com.dentner.core.cmmn.dto;

import lombok.Data;

@Data
public class FileDto {
    private int fileNo;
    private String fileTp;
    private int fileFromNo;
    private int fileSort;
    private String fileUrl;
    private String fileName;
    private String fileOrgName;
    private String fileBase64;
    private int createdNo;
}

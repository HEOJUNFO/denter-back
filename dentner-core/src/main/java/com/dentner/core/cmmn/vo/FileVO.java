package com.dentner.core.cmmn.vo;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Data
public class FileVO{
    private int fileNo;
    private String fileTp;
    private int fileFromNo;
    private int fileSort;
    private String fileUrl;
    private String fileName;
    private String fileOrgName;
}

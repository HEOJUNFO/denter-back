package com.dentner.core.cmmn.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class S3FileVO {
    private int fileNo;
    private String fileSe;
    private int fileFromNo;
    private int fileOrdr;
    private int registerNo;
    private String fileUrl;
    private String fileName;
    private String fileRealName;
    private long fileSize;
}

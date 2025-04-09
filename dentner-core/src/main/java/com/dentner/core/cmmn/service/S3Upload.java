package com.dentner.core.cmmn.service;

import com.dentner.core.cmmn.vo.S3FileVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;


public interface S3Upload {

	S3FileVO upload(InputStream inputStream, String originFileName, String fileSize);
	
	S3FileVO upload(MultipartFile multipartFile);

    S3FileVO uploadToOrgName(MultipartFile file, String fileOrgName);

	S3FileVO uploadToEncrypt(MultipartFile multipartFile) throws Exception;
}

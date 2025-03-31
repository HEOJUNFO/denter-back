package com.dentner.core.cmmn.service;

import com.dentner.core.cmmn.dto.FileDto;
import com.dentner.core.cmmn.vo.FileVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Service("fileUploadService")
public class FileUploadService {
	
	// 기본 저장 파일 경로
    @Value("${file.base-save-path}")
    private String baseSavePath;
    public String os = System.getProperty("os.name").toLowerCase();

    private static String getUuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

	public FileDto upload(MultipartFile uploadFile, String path) throws IOException {
		FileDto fileDto = new FileDto();
        String origName = uploadFile.getOriginalFilename();
        File file = null;
        String fileSavePath = "", filePath = "";
        Path uploadDir = null;
        
        try {
            // 확장자를 찾기 위한 코드
            String ext = origName.substring(origName.lastIndexOf('.'));
            // 파일이름 암호화
            String saveFileName = getUuid() + ext;

            if(os.contains("win")) {
            	fileSavePath = "c:" +baseSavePath + path + "/" + saveFileName;
    		}else {
    			fileSavePath = baseSavePath + path + "/" + saveFileName;
    		}
			filePath = path + "/" + saveFileName;
			uploadDir = Paths.get(baseSavePath + path);
            
			if(!Files.isDirectory(uploadDir)) {
				try {
					Files.createDirectories(uploadDir);
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
			
            // 파일 객체 생성
            file = new File(fileSavePath);
            
            // 파일 변환
            uploadFile.transferTo(file);
            
            //파일권한적용
        	file.setWritable(true); //쓰기가능설정
        	file.setReadable(true);	//읽기가능설정
        	if(!os.contains("win")) {
				Runtime.getRuntime().exec("chmod -R 775 " + file);	
			}

			fileDto.setFileUrl(filePath);
			fileDto.setFileName(saveFileName);
			fileDto.setFileOrgName(origName);
        } catch (StringIndexOutOfBoundsException e) {
        } 
        return fileDto;
    }

	public FileVO uploadToBase64(String fileName, String fileBase64, String path) throws IOException {
		FileVO fileVO = new FileVO();
		String origName = fileName;
		File file = null;
		String fileSavePath = "", filePath = "";
		Path uploadDir = null;

		try {
			// 확장자를 찾기 위한 코드
			String ext = origName.substring(origName.lastIndexOf('.'));
			// 파일이름 암호화
			String saveFileName = getUuid() + ext;

			if(os.contains("win")) {
				fileSavePath = "c:" +baseSavePath + path + "/" + saveFileName;
			}else {
				fileSavePath = baseSavePath + path + "/" + saveFileName;
			}
			filePath = path + "/" + saveFileName;
			uploadDir = Paths.get(baseSavePath + path);

			if(!Files.isDirectory(uploadDir)) {
				try {
					Files.createDirectories(uploadDir);
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}

			// 파일 객체 생성
			file = new File(fileSavePath);

			// 파일 변환
			// BASE64를 일반 파일로 변환하고 저장합니다.
			Base64.Decoder decoder = Base64.getDecoder();
			byte[] decodedBytes = decoder.decode(fileBase64.getBytes());
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			fileOutputStream.write(decodedBytes);
			fileOutputStream.close();

			//파일권한적용
			file.setWritable(true); //쓰기가능설정
			file.setReadable(true);	//읽기가능설정
			if(!os.contains("win")) {
				Runtime.getRuntime().exec("chmod -R 775 " + file);
			}

			fileVO.setFileUrl(filePath);
			fileVO.setFileName(saveFileName);
			fileVO.setFileOrgName(origName);
		} catch (StringIndexOutOfBoundsException e) {
		}
		return fileVO;
	}

}

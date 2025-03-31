package com.dentner.admin.api.file;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.dentner.core.cmmn.service.S3Upload;
import com.dentner.core.cmmn.vo.S3FileVO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3UploadService implements S3Upload {

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	private final AmazonS3Client s3Client;

	@Override
	public S3FileVO upload(InputStream inputStream, String originFileName, String fileSize) {
		/* 샘플
		// image 체크       
        if (!originFileName.substring(originFileName.length()-4).equals(".jpg") &&
                !originFileName.substring(originFileName.length()-4).equals(".png")){
            throw new BusinessLogicException(ExceptionCode.NOT_IMAGE_EXTENSION);
        }

        // 저장할 파일 이름 설정
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss-SSS");
        // "/"를 이용해 파일 하위 폴더 설정
        String s3FileName = dir + "/" +LocalDateTime.now().format(format)+ "-" + originFileName;
		*/
		String s3FileName = "";
		ObjectMetadata objMeta = new ObjectMetadata();
		objMeta.setContentLength(Long.parseLong(fileSize));

		s3Client.putObject(bucket, s3FileName, inputStream, objMeta);

		String url = s3Client.getUrl(bucket, s3FileName).toString();

		S3FileVO s3FileVO = new S3FileVO();

		return s3FileVO;
	}

	@Override
	public S3FileVO upload(MultipartFile multipartFile) {
		S3FileVO s3FileVO = new S3FileVO();

		String originalFilename = multipartFile.getOriginalFilename();

		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(multipartFile.getSize());
		metadata.setContentType(multipartFile.getContentType());

		try {
			String fileExtension = FilenameUtils.getExtension(originalFilename);
			String uuid = UUID.randomUUID().toString()+"."+fileExtension;

			s3Client.putObject(bucket, uuid, multipartFile.getInputStream(), metadata);
			String url = s3Client.getUrl(bucket, uuid).toString();

			s3FileVO.setFileUrl(url);
			s3FileVO.setFileRealName(originalFilename);
			s3FileVO.setFileName(uuid);
			s3FileVO.setFileSize(multipartFile.getSize());

		} catch (AmazonServiceException e) {
			e.printStackTrace();
		} catch (SdkClientException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return s3FileVO;
	}

	public S3FileVO uploadToOrgName(MultipartFile multipartFile, String orgName) {
		S3FileVO s3FileVO = new S3FileVO();
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(multipartFile.getSize());
		metadata.setContentType(multipartFile.getContentType());

		try {
			String fileExtension = FilenameUtils.getExtension(orgName);
			String uuid = UUID.randomUUID().toString()+"."+fileExtension;

			s3Client.putObject(bucket, uuid, multipartFile.getInputStream(), metadata);
			String url = s3Client.getUrl(bucket, uuid).toString();

			s3FileVO.setFileUrl(url);
			s3FileVO.setFileRealName(orgName);
			s3FileVO.setFileName(uuid);

		} catch (AmazonServiceException e) {
			e.printStackTrace();
		} catch (SdkClientException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return s3FileVO;
	}

	@Override
	public S3FileVO uploadToEncrypt(MultipartFile multipartFile) throws Exception{
		S3FileVO s3FileVO = new S3FileVO();

		return s3FileVO;
	}
}

package com.dentner.front.api.file;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.dentner.core.util.EncryptionUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class S3DownloadService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
	
	private final AmazonS3Client s3Client;

//	public ResponseEntity<ByteArrayResource> getObject(String storedFileName, String downloadFileName, HttpServletRequest req) throws IOException{
//
//		S3Object o = s3Client.getObject(new GetObjectRequest(bucket, storedFileName));
//		S3ObjectInputStream objectInputStream = o.getObjectContent();
//		byte[] bytes = IOUtils.toByteArray(objectInputStream);
//
//		String fileName = getEncodedFilename(req, downloadFileName);
//
//		InputStreamResource inputStreamResource = new InputStreamResource(objectInputStream);
//		ByteArrayResource resource = new ByteArrayResource(bytes);
//		HttpHeaders httpHeaders = new HttpHeaders();
//		httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//		httpHeaders.setContentLength(bytes.length);
//		httpHeaders.setContentDisposition(ContentDisposition.builder("attachment").filename(fileName).build());
//
//		return ResponseEntity
//				.ok()
//				.headers(httpHeaders)
//				.body(resource);
//	}
public ResponseEntity<StreamingResponseBody> getObject(String storedFileName, String downloadFileName, HttpServletRequest req) throws IOException {

	S3Object o = s3Client.getObject(new GetObjectRequest(bucket, storedFileName));
	S3ObjectInputStream objectInputStream = o.getObjectContent();

	String fileName = getEncodedFilename(req, downloadFileName);

	HttpHeaders httpHeaders = new HttpHeaders();
	httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
	httpHeaders.setContentDisposition(ContentDisposition.builder("attachment").filename(fileName).build());

	StreamingResponseBody stream = outputStream -> {
		byte[] buffer = new byte[1024]; // 버퍼 크기를 1KB로 설정
		int bytesRead;
		while ((bytesRead = objectInputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, bytesRead);
			outputStream.flush(); // 바로바로 클라이언트로 전송
		}
		objectInputStream.close();
	};

	return ResponseEntity
			.ok()
			.headers(httpHeaders)
			.body(stream); // 스트리밍 방식으로 클라이언트에 데이터 전달
}

	public ResponseEntity<ByteArrayResource> getObjectEncrypt(String storedFileName, String downloadFileName, HttpServletRequest req) throws Exception{

		// S3에서 암호화된 파일을 가져옴
		S3Object o = s3Client.getObject(new GetObjectRequest(bucket, storedFileName));
		S3ObjectInputStream objectInputStream = o.getObjectContent();

		// AES SecretKey 가져오기
		SecretKey secretKey = getSecretKey();

		// 파일 복호화
		InputStream decryptedInputStream = decryptFile(objectInputStream, secretKey);
		byte[] bytes = IOUtils.toByteArray(decryptedInputStream);

		// 다운로드할 파일 이름 인코딩
		String fileName = getEncodedFilename(req, downloadFileName);

		// 파일을 ByteArrayResource로 변환하여 응답
		ByteArrayResource resource = new ByteArrayResource(bytes);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		httpHeaders.setContentLength(bytes.length);
		httpHeaders.setContentDisposition(ContentDisposition.builder("attachment").filename(fileName).build());

		return ResponseEntity
				.ok()
				.headers(httpHeaders)
				.body(resource);
	}

	private String getEncodedFilename(HttpServletRequest request, String displayFileName) throws UnsupportedEncodingException {
		String header = request.getHeader("User-Agent");

		String encodedFilename = null;
		if (header.indexOf("MSIE") > -1) {
			encodedFilename = URLEncoder.encode(displayFileName, "UTF-8").replaceAll("\\+", "%20");
		} else if (header.indexOf("Trident") > -1) {
			encodedFilename = URLEncoder.encode(displayFileName, "UTF-8").replaceAll("\\+", "%20");
		} else if (header.indexOf("Chrome") > -1) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < displayFileName.length(); i++) {
				char c = displayFileName.charAt(i);
				if (c > '~') {
					sb.append(URLEncoder.encode("" + c, "UTF-8"));
				} else {
					sb.append(c);
				}
			}
			encodedFilename = sb.toString();
		} else if (header.indexOf("Opera") > -1) {
			encodedFilename = "\"" + new String(displayFileName.getBytes("UTF-8"), "8859_1") + "\"";
		} else if (header.indexOf("Safari") > -1) {
			encodedFilename = URLDecoder.decode("\"" + new String(displayFileName.getBytes("UTF-8"), "8859_1") + "\"", "UTF-8");
		} else {
			encodedFilename = URLDecoder.decode("\"" + new String(displayFileName.getBytes("UTF-8"), "8859_1") + "\"", "UTF-8");
		}
		return encodedFilename;

	}

	private SecretKey getSecretKey() {
		byte[] decodedKey = Base64.getDecoder().decode(EncryptionUtil.secretAesKey.getBytes());
		return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
	}

	// 파일 복호화 (참고용, 나중에 복호화 시)
	private InputStream decryptFile(InputStream encryptedInputStream, SecretKey secretKey) throws Exception {
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, secretKey);

		byte[] inputBytes = org.apache.commons.io.IOUtils.toByteArray(encryptedInputStream);
		byte[] outputBytes = cipher.doFinal(inputBytes);

		return new ByteArrayInputStream(outputBytes);
	}

}

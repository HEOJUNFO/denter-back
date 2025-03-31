package com.dentner.admin.api.file;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

@Service
@RequiredArgsConstructor
public class S3DownloadService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
	
	private final AmazonS3Client s3Client;

	public ResponseEntity<ByteArrayResource> getObject(String storedFileName, String downloadFileName, HttpServletRequest req) throws IOException{

		S3Object o = s3Client.getObject(new GetObjectRequest(bucket, storedFileName));
		S3ObjectInputStream objectInputStream = o.getObjectContent();
		byte[] bytes = IOUtils.toByteArray(objectInputStream);

		String fileName = getEncodedFilename(req, downloadFileName);

		InputStreamResource inputStreamResource = new InputStreamResource(objectInputStream);
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

}

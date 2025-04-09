package com.dentner.core.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class MailHandler {

	private final JavaMailSender sender;
	private final MimeMessage message;
	private final MimeMessageHelper msgHelper;
	
	public MailHandler(JavaMailSender sender) throws MessagingException {
		this.sender = sender;
		message = sender.createMimeMessage();
		msgHelper = new MimeMessageHelper(message, true, "UTF-8");
	}

	// 보내는 사람 이메일
	public void setFrom(String fromAddress) throws MessagingException {
		msgHelper.setFrom(fromAddress);
	}

	// 받는 사람 이메일
	public void setTo(String email) throws MessagingException {
		msgHelper.setTo(email);
	}
	
	// 제목
	public void setSubject(String subject) throws MessagingException {
		msgHelper.setSubject(subject);
	}
	
	// 메일 내용
	public void setText(String text, boolean useHtml) throws MessagingException {
		msgHelper.setText(text, useHtml);
	}

	// 첨부 파일
	public void setAttach(String displayFileName, MultipartFile file) throws MessagingException {
		msgHelper.addAttachment(displayFileName, file);
	}

	// 이미지 삽입
	public void setInline(String contentId, MultipartFile file) throws MessagingException, IOException {
		msgHelper.addInline(contentId, new ByteArrayResource(file.getBytes()), "image/jpeg");
	}

	public void setBcc(String[] bccArr) throws MessagingException {
		msgHelper.setBcc(bccArr);
	}
	
	// 발송
	public void send() {
		try {
			sender.send(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
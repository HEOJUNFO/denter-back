package com.dentner.core.cmmn.service;


import com.dentner.core.cmmn.dto.MailDto;
import com.dentner.core.mail.MailHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service
public class MailService {

	@Autowired(required = false)
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromAddress;

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Async
    public void mailSend(MailDto mailDto) throws Exception{
		try {
			MailHandler mailHandler = new MailHandler(mailSender);
			mailHandler.setTo(mailDto.getMailTo());
			mailHandler.setFrom(fromAddress);
			mailHandler.setSubject(mailDto.getMailSubject());
			mailHandler.setText(mailDto.getMailContent(), true);
			mailHandler.send();
		} catch (Exception e) {
			log.error("Failed to send mail to: {}. Error: {}", mailDto.getMailTo(), e.getMessage(), e);
		}

    }
}

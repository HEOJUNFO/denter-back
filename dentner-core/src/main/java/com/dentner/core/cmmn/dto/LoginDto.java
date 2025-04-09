package com.dentner.core.cmmn.dto;

import lombok.Data;

@Data
public class LoginDto {

	private String memberEmail;
	private String memberPassword;
	private String socialSe;
	private String uniqueKey;
	private String memberSe;
	private String fcmToken;
}

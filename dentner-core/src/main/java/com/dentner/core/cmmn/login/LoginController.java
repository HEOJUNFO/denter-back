package com.dentner.core.cmmn.login;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("coreLoginContoller")
public class LoginController {

	@PostMapping("/login1")
	public String login() {
		return "login";
	}
}

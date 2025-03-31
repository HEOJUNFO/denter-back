package com.dentner.admin.api.login;


import com.dentner.admin.version.V1ApiVersion;
import com.dentner.core.cmmn.dto.LoginDto;
import com.dentner.core.cmmn.dto.TokenDto;
import com.dentner.core.cmmn.service.response.ResponseMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Admin 로그인", description = "Admin 로그인 API")
public class AdminLoginController implements V1ApiVersion {

	@Resource(name= "adminLoginService")
	AdminLoginService adminLoginService;
	
	@PostMapping("/login")
	@ResponseMessage("로그인 성공")
	@Operation(summary = "로그인", description = "로그인 토큰 발급")
	@Parameters({
			@Parameter(name = "memberEmail", description = "사용자 이메일", example = "" ,  schema = @Schema(type = "string")),
			@Parameter(name = "memberPassword", description = "패스워드", example = "" ,  schema = @Schema(type = "string"))
	})
	public TokenDto login(@RequestBody LoginDto loginDto)throws Exception {
		return adminLoginService.loginByToken(loginDto);
	}
}

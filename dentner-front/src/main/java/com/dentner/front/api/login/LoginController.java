package com.dentner.front.api.login;


import com.dentner.core.cmmn.dto.LoginDto;
import com.dentner.core.cmmn.dto.ResetPasswordDto;
import com.dentner.core.cmmn.dto.TokenDto;
import com.dentner.core.cmmn.service.response.ResponseMessage;
import com.dentner.front.version.V1ApiVersion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "유저 로그인", description = "유저 로그인 API")
public class LoginController implements V1ApiVersion {

	@Resource(name= "loginService")
	LoginService loginService;
	
	@PostMapping("/login")
	@ResponseMessage("로그인 성공")
	@Operation(summary = "로그인", description = "로그인 토큰 발급")
	@Parameters({
	        @Parameter(name = "memberEmail", description = "사용자 이메일", example = "",  schema = @Schema(type = "string")),
	        @Parameter(name = "memberPassword", description = "패스워드", example = "",  schema = @Schema(type = "string"))
	})
	public TokenDto login(@RequestBody LoginDto loginDto) {
		return loginService.loginByToken(loginDto);
	}

	@PostMapping("/login/social")
	@ResponseMessage("구글 로그인 성공")
	@Operation(summary = "구글 로그인", description = "구글 로그인 토큰 발급")
	@Parameters({
			@Parameter(name = "socialSe", description = "social 타입", example = "",  schema = @Schema(type = "string")),
			@Parameter(name = "uniqueKey", description = "유니크 ID", example = "",  schema = @Schema(type = "string"))
	})
	public TokenDto socialLogin(@RequestBody LoginDto loginDto) {
		return loginService.loginByToken(loginDto);
	}


	@PutMapping("/reset-password")
	@ResponseMessage("비밀번호 초기화 성공")
	@Operation(summary = "비밀번호 초기화 성공", description = "비밀번호를 초기화 한다.")
	@Parameters({
			@Parameter(name = "e", description = "암호화된 이메일", example = "",  schema = @Schema(type = "string")),
			@Parameter(name = "password", description = "변경할 비밀번호", example = "",  schema = @Schema(type = "string"))
	})
	public int putResetPassword(@RequestBody ResetPasswordDto resetPasswordDto){
		return loginService.putResetPassword(resetPasswordDto);
	}
}

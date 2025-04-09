package com.dentner.admin.api.login;

import com.dentner.core.cmmn.dto.LoginDto;
import com.dentner.core.cmmn.dto.TokenDto;
import com.dentner.core.cmmn.service.MemberService;
import com.dentner.core.cmmn.vo.MemberVo;
import com.dentner.core.config.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminLoginService {
	private final AuthenticationManagerBuilder authenticationManagerBuilder;
	private final JwtTokenProvider jwtTokenProvider;
	private final MemberService memberService;
	
	public TokenDto loginByToken(LoginDto loginDto) throws Exception{
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginDto.getMemberEmail(), loginDto.getMemberPassword());
		Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
		TokenDto tokenDto = jwtTokenProvider.generateToken(authentication);

		MemberVo userInfo = memberService.getUserInfoAdmin(loginDto.getMemberEmail());
		if(!"D".equals(userInfo.getMemberSe())){
			tokenDto = null;
			throw new Exception("일치하는 관리자 정보가 없습니다.");
		}
		tokenDto.setUserInfo(userInfo);

		return tokenDto;
	}
}

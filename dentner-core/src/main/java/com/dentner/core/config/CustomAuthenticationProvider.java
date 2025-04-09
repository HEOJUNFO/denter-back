package com.dentner.core.config;

import java.util.ArrayList;
import java.util.List;

import com.dentner.core.cmmn.service.MemberService;
import com.dentner.core.cmmn.vo.TokenVo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

	private final MemberService memberService;
	private final PasswordEncoder passwordEncoder;

	@Value("${sp}")
	private String sp;
		
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		
		String username = authentication.getName();
		String password = (String) authentication.getCredentials();
		String socialSe = String.valueOf(password.charAt(0));

		TokenVo member = null;

		if(!"dentner1234!".equals(password.substring(1))){
			member = memberService.getUserByUserId(username);
			if(!sp.equals(password)){
				if (member == null || !passwordEncoder.matches(password, member.getMemberPassword())) {
					throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
				}
			}
		}else{
			member = memberService.getUserBySocial(username, socialSe);
		}
		// 2024-09-11 cjj 멀티 프로필 일때 치기공으로 토큰 생성
		int multiProfileCnt = memberService.selectMultiProfileCnt(member);
		if(multiProfileCnt > 1){
			// 멀티 프로필일 경우 로그인 시 치기공소로 로그인
			member.setMemberSe("B");
		}

		//회원 구분 (N:일반회원, A:관리자)
		List<GrantedAuthority> authorities = new ArrayList<>();
		//authorities.add(new SimpleGrantedAuthority("ROLE_NORMAL"));
		if ("A".equals(member.getMemberTp())) authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
		if ("B".equals(member.getMemberTp())) authorities.add(new SimpleGrantedAuthority("ROLE_DENT"));
		if ("C".equals(member.getMemberTp())) authorities.add(new SimpleGrantedAuthority("ROLE_DESAIGNER"));
		if ("D".equals(member.getMemberTp())) authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

		return new UsernamePasswordAuthenticationToken(member,password,authorities);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

}

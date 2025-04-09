package com.dentner.front.api.login;

import com.dentner.core.cmmn.dto.*;
import com.dentner.core.cmmn.mapper.MemberMapper;
import com.dentner.core.cmmn.service.MemberService;
import com.dentner.core.cmmn.vo.MemberVo;
import com.dentner.core.config.JwtTokenProvider;
import com.dentner.core.util.EmailUtil;
import com.dentner.front.api.common.CommonService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoginService {
	private final AuthenticationManagerBuilder authenticationManagerBuilder;
	private final JwtTokenProvider jwtTokenProvider;
	private final MemberService memberService;

	@Autowired
	MemberMapper memberMapper;
	@Resource(name= "commonService")
	CommonService commonService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public TokenDto loginByToken(LoginDto loginDto) {
		TokenDto tokenDto = null;
		UsernamePasswordAuthenticationToken authenticationToken = null;

		// 1. sns 정보를 받아 회원가입 & 로그인 여부를 판단한다.
		if(loginDto.getUniqueKey() != null){
			int result = memberMapper.selectIsSnsLogin(loginDto);
			if(result > 0) { // 로그인
				authenticationToken = new UsernamePasswordAuthenticationToken(loginDto.getUniqueKey(), loginDto.getSocialSe()+"dentner1234!");
			}else{
				return tokenDto;
			}
		}else{
			authenticationToken = new UsernamePasswordAuthenticationToken(loginDto.getMemberEmail(), loginDto.getMemberPassword());
		}

	    Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        tokenDto = jwtTokenProvider.generateToken(authentication);

		// 2024-09-11 CJJ 현재 권한을 얻어온다. 멀티일 경우에는 치과기공소로
		String memberSe = memberService.getMemberSe(loginDto);
		MemberVo memberVo = null;
		if(loginDto.getUniqueKey() != null){
			loginDto.setMemberSe(memberSe);
			memberVo = memberService.getUserSocialInfo(loginDto);
		}else{
			memberVo = memberService.getUserInfo(loginDto.getMemberEmail(), memberSe);
		}
		
		// 2024-09-24 PSH 블랙리스트 OR 가입승인 전 인 경우 token null 처리
		if (!"B".equals(memberVo.getMemberApprovalSe()) || "Y".equals(memberVo.getBlackYn())) {
			tokenDto = TokenDto.builder()
	                .grantType(null)
	                .authorizationType(null)
	                .accessToken(null)
	                .accessTokenExpiresIn(null)
	                .refreshToken(null)
	                .build();
		}else{
			// last login dt update
			memberService.updateLastLoginDt(memberVo);
			if(loginDto.getFcmToken() != null && !"".equals(loginDto.getFcmToken())){
				memberVo.setFcmToken(loginDto.getFcmToken());
				memberService.insertFcmToken(memberVo);
			}
		}
		
		tokenDto.setUserInfo(memberVo);

		return tokenDto;
	}

	public int postAuthMail(MailDto mailDto) {
		String email = mailDto.getMailTo();
		// 1. 이메일 주소가 있는지 확인.
		int result = memberMapper.selectUserEmail(email);

		if(result == 0 ){
			return result;
		}
		// 2. 암호화된 링크 생성
		String encryptedEmail = EmailUtil.encryptEmail(email);
		String token = UUID.randomUUID().toString();
		// 3. DB에 TOKEN 저장
		PassWordResetDto passWordResetDto = new PassWordResetDto();
		passWordResetDto.setEmail(email);
		passWordResetDto.setTokenValue(token);

		int resetResult = memberMapper.insertPasswordReset(passWordResetDto);

		if(resetResult == 0){
			return resetResult;
		}

		String param = token;
		try{
			param = URLEncoder.encode(token.replace("+", "%2B"), "UTF-8");
			param = param+"&e="+URLEncoder.encode(encryptedEmail.replace("+", "%2B"), "UTF-8");

		} catch (UnsupportedEncodingException e){
			e.printStackTrace();
		}
		String emailTemplate = EmailUtil.readHTMLTemplate(param);
		mailDto.setMailSubject("비밀번호 초기화 메일 입니다.");
		mailDto.setMailContent(emailTemplate);
		// 3. 메일 보내기
		commonService.postMail(mailDto);

		return result;

	}

	public int getAuthMail(String el, String token) {
		String encodedData = "";
		try {
			encodedData = URLDecoder.decode(el, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			System.err.println("Error encoding data: " + e.getMessage());
		}
		String email = EmailUtil.decryptEmail(encodedData);
		int result = memberMapper.selectResetPassword(email, token);

		return result;
	}

	public int putResetPassword(ResetPasswordDto resetPasswordDto) {
		String encodedData = "";
		try {
			String replacedData = resetPasswordDto.getE().replace("%2B", "+");
			encodedData = URLDecoder.decode(resetPasswordDto.getE(), "UTF-8");
			encodedData = encodedData.replace("%2B", "+");
		} catch (UnsupportedEncodingException e) {
			System.err.println("Error encoding data: " + e.getMessage());
		}
		String email = EmailUtil.decryptEmail(encodedData);
		resetPasswordDto.setEmail(email);
		resetPasswordDto.setPassword(passwordEncoder.encode(resetPasswordDto.getPassword()));
		int result = memberMapper.updateResetPassword(resetPasswordDto);
		if(result == 0){
			return result;
		}

		memberMapper.deleteResetPassword(resetPasswordDto);

		return result;
	}
}

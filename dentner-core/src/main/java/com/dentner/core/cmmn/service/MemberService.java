package com.dentner.core.cmmn.service;


import com.dentner.core.cmmn.dto.LoginDto;
import com.dentner.core.cmmn.mapper.MemberMapper;
import com.dentner.core.cmmn.vo.MemberVo;
import com.dentner.core.cmmn.vo.TokenVo;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
	
	private final PasswordEncoder passwordEncoder;
	
	@Resource(name="memberMapper")
	private MemberMapper memberMapper;
	
	public TokenVo getUserByUserEmail(String email) {
		return memberMapper.selectLoginMember(email);
		
	}

	public TokenVo getUserByUserId(String memberId) {
		return memberMapper.selectLoginMemberId(memberId);

	}

	public MemberVo getUserInfo(String memberId, String memberSe){
		return memberMapper.selectUserInfo(memberId, memberSe);
	}
	
	public String postProduct() {
		//String encodedPassword = passwordEncoder.encode(password);
		
		return "";
	}

	public TokenVo getUserBySocial(String uniqueKey, String socialSe) {
		return memberMapper.selectUserBySocial(uniqueKey, socialSe);
	}

	public MemberVo getUserSocialInfo(LoginDto loginDto) {
		return memberMapper.selectUserSocialInfo(loginDto);
	}

	public int selectMultiProfileCnt(TokenVo member) {
		return memberMapper.selectMultiProfileCnt(member);
	}

	public MemberVo getUserInfoNo(int memberNo, String memberSe) {
		return memberMapper.selectUserInfoNo(memberNo, memberSe);
	}

	public String getMemberSe(LoginDto loginDto) {
		return memberMapper.selectMemberSe(loginDto);
	}

	public MemberVo getUserInfoAdmin(String memberEmail) {
		return memberMapper.selectUserInfoAdmin(memberEmail);
	}

    public void updateLastLoginDt(MemberVo memberVo) {
		memberMapper.updateLastLoginDt(memberVo);
    }
    
    public void insertFcmToken(MemberVo memberVo) {
    	memberMapper.insertFcmToken(memberVo);
    }
}

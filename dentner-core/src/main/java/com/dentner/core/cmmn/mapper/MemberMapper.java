package com.dentner.core.cmmn.mapper;

import com.dentner.core.cmmn.dto.AuthCodeDto;
import com.dentner.core.cmmn.dto.LoginDto;
import com.dentner.core.cmmn.dto.PassWordResetDto;
import com.dentner.core.cmmn.dto.ResetPasswordDto;
import com.dentner.core.cmmn.vo.MemberVo;
import com.dentner.core.cmmn.vo.PhoneVo;
import com.dentner.core.cmmn.vo.TokenVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface MemberMapper {

	TokenVo selectLoginMember(String email);
	TokenVo selectLoginMemberId(String userId);

	MemberVo selectUserInfo(@Param("memberId")String memberId, @Param("memberSe") String memberSe);

	int selectUserEmail(String email);

	int insertPasswordReset(PassWordResetDto passWordResetDto);

	int selectResetPassword(@Param("email") String email, @Param("token")String token);

	int updateResetPassword(ResetPasswordDto resetPasswordDto);

	void deleteResetPassword(ResetPasswordDto resetPasswordDto);

	int selectUserPhone(@Param("phone") String phone, @Param("contactNationCode") int contactNationCode);

	int insertAuthCode(AuthCodeDto authCodeDto);

	PhoneVo selectAuthPhone(@Param("authCode") String authCode, @Param("token") String token);

	MemberVo selectUserInfoPhone(PhoneVo phoneVo);

	String selectNationCode(@Param("contactNationCode") int contactNationCode);

	int selectIsSnsLogin(LoginDto loginDto);

	TokenVo selectUserBySocial(@Param("uniqueKey") String uniqueKey, @Param("socialSe") String socialSe);

	MemberVo selectUserSocialInfo(LoginDto loginDto);

    int selectMultiProfileCnt(TokenVo member);

    MemberVo selectUserInfoNo(@Param("memberNo") int memberNo, @Param("memberSe") String memberSe);

	String selectMemberSe(LoginDto loginDto);

	MemberVo selectUserInfoAdmin(@Param("memberEmail") String memberEmail);

    void updateLastLoginDt(MemberVo memberVo);
    
    void insertFcmToken(MemberVo memberVo);
}

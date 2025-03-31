package com.dentner.core.cmmn.mapper;


import com.dentner.core.cmmn.dto.*;
import com.dentner.core.cmmn.vo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FrontMypageMapper {
    MemberVo selectMypage(@Param("memberNo") int memberNo, @Param("memberSe") String memberSe);

    int updateMypageOut(@Param("memberNo") int memberNo);

    MemberProfileVo selectMypageProfile(@Param("memberNo") int memberNo, @Param("memberSe") String memberSe);

    int updateMypageMember(MemberProfileAddDto memberProfileAddDto);

    void updateMypageProfile(MemberProfileAddDto memberProfileAddDto);

    void updateMypageSw(MemberProfileAddDto memberProfileAddDto);

    List<InterestVo> selectInterestList(InterestDto interestDto);

    int selectInterestListCnt(InterestDto interestDto);

    List<BlockVo> selectBlockList(BlockDto blockDto);

    int selectBlockListCnt(BlockDto blockDto);

    int updateMypageType(MemberTypeDto memberTypeDto);

    List<MemberTypeVo> selectMemberTypeList(@Param("memberNo") Integer memberNo);

    List<ReviewVo> selectReviewList(ReviewDto reviewDto);

    int selectReviewListCnt(ReviewDto reviewDto);

    int deleteMypageReview(@Param("reviewNo")Integer reviewNo, @Param("memberNo")int memberNo);

    int updateReview(ReviewDto reviewDto);

    ReviewVo selectReviewDetail(@Param("reviewNo")Integer reviewNo, @Param("memberNo")int memberNo, @Param("memberSe") String memberSe);

    int updateMypagePhone(PhoneDto phoneDto);

    String selectMypagePassword(PasswordDto passwordDto);

    int updateMypagePassword(PasswordDto passwordDto);

    int updateMypageMemberInfo(MemberAddDto memberAddDto);

    int selectMypageOutEnd( @Param("memberNo")int memberNo);

    int selectProfileDesigner(@Param("memberNo")int memberNo);

    int insertProfileDesigner(@Param("memberNo")int memberNo, @Param("memberSe")String memberSe);

    int insertProfileDesignerSw(@Param("memberNo")int memberNo);

    List<AlarmCodeVo> selectAlarmList(@Param("memberNo")int memberNo, @Param("memberSe")String memberSe, @Param("type")String type);

    int insertAlarmSetting(@Param("code")String code, @Param("memberNo")int memberNo);

    int deleteAlarmSetting(@Param("code")String code, @Param("memberNo")int memberNo);

    int updateProfileShow(@Param("type") String type, @Param("memberSe") String memberSe, @Param("memberNo") int memberNo);

    void insertMemberOut(@Param("memberNo")int memberNo);

    int selectMypageOutMileage(@Param("memberNo")int memberNo);

    void updateProfileDesignerName(@Param("memberNo")int memberNo);

	int selectPhoneCheck(PhoneDto phoneDto);

    List<ReviewVo> selectReviewMemberList(ReviewDto reviewDto);

    int selectReviewMemberListCnt(ReviewDto reviewDto);

    List<AlarmCodeVo> selectAlarmListNew(@Param("memberNo")int memberNo, @Param("memberSe")String memberSe, @Param("type")String type);

    int insertAlarmAllSetting(@Param("code")String code, @Param("memberNo")int memberNo, @Param("memberSe")String memberSe);

    int deleteAlarmAllSetting(@Param("code")String code, @Param("memberNo")int memberNo, @Param("memberSe")String memberSe);
}

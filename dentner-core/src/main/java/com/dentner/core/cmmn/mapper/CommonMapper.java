package com.dentner.core.cmmn.mapper;

import com.dentner.core.cmmn.dto.*;
import com.dentner.core.cmmn.vo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommonMapper {
    List<CodeVo> selectCodeList(@Param("parentNo") Integer parentNo, @Param("type") String type);

    int insertAuthCode(AuthCodeDto authCodeDto);

    PhoneVo selectAuthPhone(String authCode, String token);

    String selectNationCode(@Param("codeNo") Integer memberContactNation);

    int selectUserPhone(PhoneDto phoneDto);

    void updateAuthPhone(String authCode, String token);

    List<TeethTypeVo> selectTeethType(@Param("parentNo")Integer parentNo, @Param("type")String type);

    int selectAlarmCnt(@Param("memberNo")int memberNo);

    List<AlarmVo> selectAlarmList(AlarmDto alarmDto);

    int selectAlarmListCnt(AlarmDto alarmDto);

    int updateAlarmRead(@Param("memberNo")int memberNo, @Param("alarmNo") Integer alarmNo);

    int updateAlarmReadAll(@Param("memberNo")int memberNo, @Param("type") String type);

    int insertAlarm(AlarmAddDto alarmAddDto);

    AlarmTalkDto selectRequestInfo(@Param("requestFormNo") Integer requestFormNo);

    MainStatVo selectMainStatData();
    
    String selectMemberNickName(String memberNo);

    AlarmTalkDto selectEstimateInfo(@Param("requestFormNo") Integer requestFormNo, @Param("registerNo") Integer registerNo);

    AlarmTalkDto selectMileageInfo(@Param("memberNo") int memberNo);

    int insertAllAlarm(AlarmAddDto alarmAddDto);

    List<String> selectMemberFCMToken(@Param("memberNo") int memberNo);

    // 추가: 모든 회원의 FCM 토큰을 조회하는 메서드
    List<String> selectMemberAllFCMToken();

    int selectAlarm(@Param("memberNo")  int memberNo, @Param("alarmCodeNo") int alarmCodeNo);

    int selectAlarmCheckList(@Param("memberNo") int memberNo, @Param("alarmCodeList") List<String> alarmCodeList);

    String selectMileageAmount(@Param("mileageNo") int mileageNo);

    String selectMileage(@Param("mileageNo") int mileageNo);

    String selectMemberTp(@Param("memberNo") int memberNo);
}
package com.dentner.core.cmmn.mapper;

import com.dentner.core.cmmn.dto.AuthCodeDto;
import com.dentner.core.cmmn.dto.MemberAddDto;
import com.dentner.core.cmmn.dto.PasswordDto;
import com.dentner.core.cmmn.vo.TermsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface JoinMapper {

    int insertMember(MemberAddDto memberAddDto);

    int selectMember(MemberAddDto memberAddDto);

    int selectMemberPhone(MemberAddDto memberAddDto);

    TermsVo selectTerms(@Param("termsSe") String termsSe);

    int selectDupNickName(@Param("memberNickName") String memberNickName);

    void insertSocialMember(MemberAddDto memberAddDto);

    int selectDupEmail(@Param("memberEmail") String memberEmail);

    String selectEmail(@Param("token") String token);

    int updatePassword(PasswordDto passwordDto);

    void insertProfileMember(MemberAddDto memberAddDto);

    void insertSwMember(MemberAddDto memberAddDto);

    void insertAlarmSetting(MemberAddDto memberAddDto);

    void insertAlarmSettingNew(MemberAddDto memberAddDto);
}

package com.dentner.core.cmmn.mapper;

import com.dentner.core.cmmn.dto.AlarmTalkDto;
import com.dentner.core.cmmn.dto.MemberAddDto;
import com.dentner.core.cmmn.dto.MemberDto;
import com.dentner.core.cmmn.vo.AdminDocListVo;
import com.dentner.core.cmmn.vo.MemberVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminMemberMapper {
    List<MemberVo> selectMemberList(MemberDto memberDto);

    int selectMemberListCnt(MemberDto memberDto);

    MemberVo selectMemberDetail(@Param("memberNo") int memberNo);

    int updateMember(MemberAddDto memberAddDto);

    int deleteMember(@Param("memberNo") int memberNo);

    int updateApprovalMember(@Param("memberNoArr")String memberNoArr, @Param("registerNo")int registerNo);

    int updateApprovalRejectMember(@Param("memberNoArr")String memberNoArr, @Param("registerNo")int registerNo);

    int updateOutApprovalMember(@Param("memberNo")int memberNo, @Param("registerNo")int registerNo);
    
    int updateMemberOut(@Param("memberNo")int memberNo, @Param("registerNo")int registerNo);

    AlarmTalkDto selectApprovalMember(@Param("memberNo") String memberNo);

    void deleteRealMember(@Param("memberNo") String memberNo);

    List<MemberVo> selectMemberExcelList(MemberDto memberDto);
}

package com.dentner.core.cmmn.mapper;

import com.dentner.core.cmmn.dto.AdminReportDto;
import com.dentner.core.cmmn.dto.MemberAddDto;
import com.dentner.core.cmmn.dto.MemberDto;
import com.dentner.core.cmmn.vo.AdminReportVo;
import com.dentner.core.cmmn.vo.MemberVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminReportMapper {

    List<AdminReportVo> selectReportList(AdminReportDto adminReportDto);

    int selectReportListCnt(AdminReportDto adminReportDto);

    AdminReportVo selectReportDetail(@Param("reportNo") int reportNo);

    List<String> selectReportBlockCheck(@Param("memberNoArr")String memberNoArr);
    
    int insertReportBlock(@Param("memberNoArr")String memberNoArr, @Param("registerNo") Integer registerNo);

    int deleteReportBlock(@Param("memberNoArr")String memberNoArr);
}

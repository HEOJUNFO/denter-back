package com.dentner.core.cmmn.mapper;

import com.dentner.core.cmmn.dto.BbsDto;
import com.dentner.core.cmmn.dto.DentalDto;
import com.dentner.core.cmmn.dto.DesignerDto;
import com.dentner.core.cmmn.dto.ReportDto;
import com.dentner.core.cmmn.vo.BbsVo;
import com.dentner.core.cmmn.vo.DentalVo;
import com.dentner.core.cmmn.vo.DesignerVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FrontDentalMapper {
    List<DentalVo> selectDentalLaboratoryList(DentalDto dentalDto);

    int selectDentalLaboratoryListCnt(DentalDto dentalDto);

    int insertInterestDental(@Param("memberNo") int memberNo, @Param("targetNo")Integer targetNo, @Param("interestSe")String interestSe);

    int deleteInterestDental(@Param("memberNo")int memberNo, @Param("targetNo")Integer targetNo, @Param("interestSe")String interestSe);

    int insertBlockDental(@Param("memberNo") int memberNo, @Param("targetNo")Integer targetNo, @Param("blockSe")String blockSe);

    int insertReportDental(ReportDto reportDto);

    DentalVo selectDentalLaboratoryDetail(@Param("memberNo") int memberNo, @Param("registerNo") int registerNo);

    List<DesignerVo> selectDentalDesignerList(DesignerDto designerDto);

    int selectDentalDesignerListCnt(DesignerDto designerDto);

    DesignerVo selectDentalDesignerDetail(@Param("memberNo") int memberNo, @Param("registerNo") int registerNo);

    int deleteBlockDental(@Param("memberNo") int memberNo, @Param("targetNo")Integer targetNo, @Param("blockSe")String blockSe);

    int selectBlockDental(@Param("memberNo") int memberNo, @Param("targetNo")Integer targetNo, @Param("blockSe")String blockSe);
}

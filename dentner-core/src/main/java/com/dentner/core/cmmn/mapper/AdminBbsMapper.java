package com.dentner.core.cmmn.mapper;

import com.dentner.core.cmmn.dto.BbsAddDto;
import com.dentner.core.cmmn.dto.BbsDto;
import com.dentner.core.cmmn.vo.BbsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminBbsMapper {
    List<BbsVo> selectBbsList(BbsDto bbsDto);

    int selectBbsListCnt(BbsDto bbsDto);

    BbsVo selectBbsDetail(@Param("bbsNo") Integer bbsNo);

    int insertBbs(BbsAddDto bbsAddDto);

    int updateBbs(BbsAddDto bbsAddDto);

    int deleteBbs(@Param("bbsNoArr") String bbsNoArr);

    int updateFixBbs(@Param("bbsNoArr") String bbsNoArr, @Param("type") String type);

    int selectFixBbs(@Param("bbsTp") String bbsTp);
}

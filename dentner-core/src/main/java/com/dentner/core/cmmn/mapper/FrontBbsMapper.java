package com.dentner.core.cmmn.mapper;

import com.dentner.core.cmmn.dto.BbsDto;
import com.dentner.core.cmmn.vo.BbsVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FrontBbsMapper {
    List<BbsVo> selectBbs(BbsDto bbsDto);

    int selectBbsCnt(BbsDto bbsDto);
}

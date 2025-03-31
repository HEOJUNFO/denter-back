package com.dentner.core.cmmn.mapper;

import com.dentner.core.cmmn.vo.BannerVo;
import com.dentner.core.cmmn.vo.BbsVo;
import com.dentner.core.cmmn.vo.StatVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FrontMainMapper {
    List<BannerVo> selectBanner();

    StatVo selectStat();

    List<BbsVo> selectBbs(@Param("bbsTp") String bbsTp);
}

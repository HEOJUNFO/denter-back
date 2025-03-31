package com.dentner.core.cmmn.mapper;

import com.dentner.core.cmmn.dto.BannerAddDto;
import com.dentner.core.cmmn.dto.BannerDto;
import com.dentner.core.cmmn.vo.BannerVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminBannerMapper {
    List<BannerVo> selectBannerList(BannerDto bannerDto);

    int selectBannerListCnt(BannerDto bannerDto);

    BannerVo selectBannerDetail(@Param("bannerNo") int bannerNo);

    int insertBanner(BannerAddDto bannerAddDto);

    int updateBanner(BannerAddDto bannerAddDto);

    int deleteBanner(@Param("bannerNoArr") String bannerNoArr);

    int updateBannerOrdr(BannerAddDto bannerAddDto);
}

package com.dentner.core.cmmn.mapper;

import com.dentner.core.cmmn.dto.AdminDocDto;
import com.dentner.core.cmmn.dto.AdminReportDto;
import com.dentner.core.cmmn.vo.AdminDocVo;
import com.dentner.core.cmmn.vo.AdminReportVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminDocMapper {
    List<AdminDocVo> selectDocList(AdminDocDto adminDocDto);

    int selectDocListCnt(AdminDocDto adminDocDto);

    AdminDocVo selectDocDetail(@Param("requestDocGroupNo") int requestDocGroupNo);
}

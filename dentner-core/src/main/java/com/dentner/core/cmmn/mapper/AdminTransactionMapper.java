package com.dentner.core.cmmn.mapper;

import com.dentner.core.cmmn.dto.*;
import com.dentner.core.cmmn.vo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface AdminTransactionMapper {
    List<AdminRequestFormVo> selectTransactionList(AdminRequestFormDto adminRequestFormDto);

    int selectTransactionListCnt(AdminRequestFormDto adminRequestFormDto);

    AdminRequestFormVo selectTransactionDetail(@Param("type") String type, @Param("requestFormNo") Integer requestFormNo);

    List<Map<String, Object>> selectTransactionDocGroup(@Param("requestFormNo") int requestFormNo);

    List<ReplyVo> selectTransactionReplyList(@Param("requestFormNo") int requestFormNo);

    List<Map<String, Object>> selectTransactionProstheticsList(@Param("requestDocGroupNo") int requestDocGroupNo);

    List<Map<String, Object>> selectTransactionDoc(@Param("requestDocGroupNo") int requestDocGroupNo);

    List<AdminRequestFormVo> selectTransctionExcelList(AdminRequestFormDto adminRequestFormDto);


}


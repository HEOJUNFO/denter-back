package com.dentner.core.cmmn.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.dentner.core.cmmn.dto.EstimateAddDto;
import com.dentner.core.cmmn.dto.EstimateTypeDto;
import com.dentner.core.cmmn.dto.OftenDto;
import com.dentner.core.cmmn.dto.ReplyAddDto;
import com.dentner.core.cmmn.dto.ReportDto;
import com.dentner.core.cmmn.dto.RequestBasketDto;
import com.dentner.core.cmmn.dto.RequestDocDto;
import com.dentner.core.cmmn.dto.RequestDocListDto;
import com.dentner.core.cmmn.dto.RequestFormAddDto;
import com.dentner.core.cmmn.dto.RequestFormDto;
import com.dentner.core.cmmn.dto.RequestFormRefuseDto;
import com.dentner.core.cmmn.dto.RequestTypeDentalDto;
import com.dentner.core.cmmn.dto.RequestTypeDetailDto;
import com.dentner.core.cmmn.dto.RequestTypeDto;
import com.dentner.core.cmmn.dto.ValueDataDto;
import com.dentner.core.cmmn.dto.ValueDto;
import com.dentner.core.cmmn.vo.MemberVo;
import com.dentner.core.cmmn.vo.OftenVo;
import com.dentner.core.cmmn.vo.ProfileVo;
import com.dentner.core.cmmn.vo.ReplyVo;
import com.dentner.core.cmmn.vo.RequestBasketVo;
import com.dentner.core.cmmn.vo.RequestEstimateDetailVo;
import com.dentner.core.cmmn.vo.RequestFormDetailVo;
import com.dentner.core.cmmn.vo.RequestFormVo;
import com.dentner.core.cmmn.vo.RequestJsonVo;
import com.dentner.core.cmmn.vo.TargetDesignerVo;
import com.dentner.core.cmmn.vo.ValueVo;

@Mapper
public interface FrontRequestMapper {

    int insertRequestOften(OftenDto oftenDto);

    List<OftenVo> selectOftenList(@Param("memberNo") Integer memberNo);

    int deleteRequestOften(@Param("memberNo") Integer memberNo, @Param("oftenNo")Integer oftenNo);

    int insertRequestValue(ValueDto valueDto);

    List<ValueVo> selectValueList(@Param("memberNo") Integer memberNo);

    int deleteRequestValue(@Param("memberNo") Integer memberNo, @Param("valueNo")Integer valueNo);

    int insertRequestDocGroup(RequestDocDto requestDocDto);

    void insertRequestDoc(RequestDocDto requestDocDto);

    void insertRequestDocDetail(RequestDocListDto requestDocListDto);


    void insertRequestType(RequestTypeDto requestTypeDto);

    String selectRequestBakNumber(@Param("registerNo") Integer registerNo);

    List<RequestBasketVo> selectTempList(@Param("type") String type, @Param("memberNo") Integer memberNo);

    List<RequestBasketVo> selectRequestBasketList(RequestBasketDto requestBasketDto);

    int selectRequestBasketListCnt(RequestBasketDto requestBasketDto);

    int insertRequestForm(RequestFormAddDto requestFormAddDto);

    List<RequestBasketVo> selectRequestDocList(@Param("type") String type, @Param("memberNo") Integer memberNo);

    List<TargetDesignerVo> selectRequestDesingerList(@Param("memberNo") Integer memberNo);

    List<RequestFormVo> selectRequestFormList(RequestFormDto requestFormDto);

    int selectRequestFormListCnt(RequestFormDto requestFormDto);

    List<HashMap<String, Object>> selectRequestFormRequestList(@Param("requestFormNo")Integer requestFormNo);

    RequestFormDetailVo selectRequestFormDetail(@Param("requestFormNo") Integer requestFormNo, @Param("memberNo") int memberNo);

    List<HashMap<String, Object>> selectRequestDetailProstheticsList(@Param("requestFormNo") Integer requestFormNo);

    List<RequestBasketVo> selectRequestDetailRequestList(@Param("requestFormNo")Integer requestFormNo, @Param("memberNo") int memberNo);

    List<ReplyVo> selectRequestDetailReplyList(@Param("requestFormNo") Integer requestFormNo, @Param("memberNo") int memberNo);

    int insertReportRequestForm(ReportDto reportDto);

    int insertRequestFormReply(ReplyAddDto replyAddDto);

    int updateRequestFormReply(ReplyAddDto replyAddDto);

    int deleteRequestFormReply(@Param("requestFormAnswerNo") Integer requestFormAnswerNo);

    int deleteRequestForm(@Param("requestFormNoArr") String requestFormNoArr);

    int selectRequestFormTargetAmount(@Param("targetNo")Integer targetNo, @Param("docList")List<String> docList);

    int insertRequestEstimate(EstimateAddDto estimateAddDto);

    int insertRequestEstimateType(EstimateTypeDto estimateTypeDto);

    int deleteRequestDoc(@Param("requestDocGroupArr") String requestDocGroupArr);

    int deleteRequestDocGroup(@Param("requestDocGroupArr") String requestDocGroupArr);

    List<HashMap<String, Object>> selectRequestEstimateProstheticsList(@Param("requestFormNo")Integer requestFormNo, @Param("memberNo")int memberNo);

    RequestEstimateDetailVo selectRequestFormEstimateDetail(@Param("requestFormNo") Integer requestFormNo, @Param("memberNo") int memberNo);

    String selectRequestFormEstimateDocDesc(@Param("requestFormNo") Integer requestFormNo);

    Integer selectRequestEstimateStatus(@Param("requestFormNo") Integer requestFormNo, @Param("memberNo") int memberNo);

    int updateRequestForm(RequestFormAddDto requestFormAddDto);

    String selectRequestBasketDocDesc(@Param("requestDocGroupNo") Integer requestDocGroupNo, @Param("memberNo") int memberNo);

    int insertRequestTargetFormRefuse(RequestFormRefuseDto requestFormRefuseDto);

    void insertRequestTypeDetail(RequestTypeDetailDto requestTypeDetailDto);

    void insertRequestTypeDental(RequestTypeDentalDto requestTypeDentalDto);

    RequestJsonVo selectRequestJson(@Param("requestDocGroupNo") Integer requestDocGroupNo, @Param("memberNo") int memberNo);

    List<Map<String, Object>> selectRequestDocFileList(@Param("requestDocGroupNo") Integer requestDocGroupNo);

    int updateRequestDocGroup(RequestDocDto requestDocDto);

    void updateRequestDoc(RequestDocDto requestDocDto);

    void deleteRequestType(RequestDocDto requestDocDto);

    void updateRequestDocDetail(RequestDocListDto requestDocListDto);

    void deleteRequestTypeDetail(RequestDocListDto requestDocListDto);

    void deleteRequestDentalDetail(RequestTypeDetailDto typeDto);

    void deleteRequestDocDetail(RequestDocListDto requestDocListDto);

    List<Map<String, Object>> selectRequestDocKeyList(Integer requestDocGroupNo);

    ProfileVo selectRequestProfile(@Param("memberNo") Integer memberNo);

    void insertRequestValueData(ValueDataDto valueDataDto);

    int selectRequestEstimateCnt(EstimateAddDto estimateAddDto);

    // chat 을 위한 쿼리 추가
    RequestFormDetailVo selectRequestFormDetailForChat(@Param("requestFormNo") Integer requestFormNo);

    // 알림톡 위한 쿼리 추가
    MemberVo selectParentAnswerInfo(ReplyAddDto replyAddDto);

    RequestJsonVo selectRequestJsonView(@Param("requestDocGroupNo") Integer requestDocGroupNo, @Param("requestFormNo") Integer requestFormNo, @Param("memberNo") Integer memberNo);
}

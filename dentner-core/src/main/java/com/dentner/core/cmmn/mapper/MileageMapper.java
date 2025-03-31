package com.dentner.core.cmmn.mapper;

import com.dentner.core.cmmn.dto.*;
import com.dentner.core.cmmn.vo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface MileageMapper {

    CardVo selectCardInfo(@Param("registerNo") int registerNo);

    int insertCard(CardAddDto cardAddDto);

    int updateCard(CardAddDto cardAddDto);

    int selectCardCnt(@Param("registerNo") int registerNo);

    int insertMileageCharge(MileageAddDto cardAddDto);

    List<MileageVo> selectMileageChargeList(MileageDto mileageDto);

    int selectMileageChargeListCnt(MileageDto mileageDto);

    List<MileageVo> selectMileagePaymentList(MileageDto mileageDto);

    int selectMileagePaymentListCnt(MileageDto mileageDto);

    int selectMileage(@Param("memberNo") int memberNo);

    int insertMileageRefund(MileageRefundAddDto mileageRefundAddDto);

    Map<String, Object> selectMileageDesigner(@Param("memberNo") int memberNo);

    Map<String, Object> selectMileageDesignerCalculate(@Param("memberNo") int memberNo);

    int updateMileageCalculate(MileageCalculateAddDto mileageCalculateAddDto);

    int insertMileageCalculate(MileageCalculateAddDto mileageCalculateAddDto);

    List<MileageDesignerVo> selectMileageDepositList(MileageDto mileageDto);

    int selectMileageDepositListCnt(MileageDto mileageDto);

    List<MileageDesignerCalculateVo> selectMileageCalculateList(MileageDto mileageDto);

    int selectMileageCalculateListCnt(MileageDto mileageDto);

    List<MileageVo> selectCalculateAllList(MileageCalculateAddDto mileageCalculateAddDto);

    int insertEasyPay(@Param("amount") Integer amount, @Param("moId") String moId, @Param("registerNo") Integer memberNo);

    int selectEasyPayment(Map<String, Object> map);

    int updateEasyPayment(Map<String, Object> map);

    int selectEasyPaymentVerification(Map<String, Object> map);

    int insertPaypal(@Param("amount") Integer amount, @Param("moId") String moId, @Param("registerNo") Integer memberNo);

    int selectPaypal(Map<String, Object> map);

    int selectPaypalPaymentVerification(Map<String, Object> map);

    int updatePaypal(Map<String, Object> map);

    List<MileageVo> selectCalculateList(MileageCalculateAddDto mileageCalculateAddDto);

    void insertMileageCalculateGroup(MileageCalculateAddDto mileageCalculateAddDto);

    void updateMileageCalculateGroup(MileageCalculateAddDto mileageCalculateAddDto);
}

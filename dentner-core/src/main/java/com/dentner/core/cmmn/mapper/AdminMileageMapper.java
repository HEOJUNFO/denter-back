package com.dentner.core.cmmn.mapper;

import com.dentner.core.cmmn.dto.AdminMileageDto;
import com.dentner.core.cmmn.dto.AdminReportDto;
import com.dentner.core.cmmn.vo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminMileageMapper {
    List<AdminChargeVo> selectChargeList(AdminMileageDto adminMileageDto);

    int selectChargeListCnt(AdminMileageDto adminMileageDto);



    List<AdminPayVo> selectPayList(AdminMileageDto adminMileageDto);

    int selectPayListCnt(AdminMileageDto adminMileageDto);

    List<AdminCalculateVo> selectCalculateList(AdminMileageDto adminMileageDto);

    int selectCalculateListCnt(AdminMileageDto adminMileageDto);

    List<AdminRefundVo> selectRefundList(AdminMileageDto adminMileageDto);

    int selectRefundListCnt(AdminMileageDto adminMileageDto);

    List<AdminDepositVo> selectDepositList(AdminMileageDto adminMileageDto);

    int selectDepositListCnt(AdminMileageDto adminMileageDto);

    int updateApprovalRefund(@Param("mileageNoArr")String mileageNoArr, @Param("registerNo")int registerNo);

    int updateCalculateConfirm(@Param("calculateNo")Integer calculateNo, @Param("memberNo")int memberNo, @Param("stat")String stat);

    void updateMileageCalculateConfirm(@Param("calculateNo")Integer calculateNo,@Param("memberNo") int memberNo, @Param("calculateSe") String calculateSe);

    int updateMileageBillConfirm(@Param("calculateNo")Integer calculateNo, @Param("memberNo")int memberNo);

    List<AdminRefundVo> selectRefundAlarmList(String mileageNoArr);

    List<AdminCalculateGroupVo> selectCalculateGroupList(AdminMileageDto adminMileageDto);

    int selectCalculateGroupListCnt(AdminMileageDto adminMileageDto);

    List<AdminCalculateVo> selectCalculateGroup(@Param("calculateGroupNo") Integer calculateGroupNo);

    void updateCalculateGroupConfirm(@Param("calculateGroupNo") Integer calculateGroupNo, @Param("memberNo") int memberNo);
}

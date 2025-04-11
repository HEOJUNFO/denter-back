package com.dentner.admin.api.mileage;


import java.util.List;

import com.dentner.core.cmmn.dto.AlarmTalkDto;
import com.dentner.core.cmmn.vo.*;
import com.dentner.core.util.AlarmTalkEnum;
import com.dentner.core.util.ConstUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dentner.admin.api.common.CommonService;
import com.dentner.core.cmmn.dto.AdminMileageDto;
import com.dentner.core.cmmn.dto.AlarmAddDto;
import com.dentner.core.cmmn.dto.PushDto;
import com.dentner.core.cmmn.mapper.AdminMileageMapper;
import com.dentner.core.util.SecurityUtil;

import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminMileageService {

	@Autowired
	AdminMileageMapper adminMileageMapper;
	
	@Resource(name= "commonService")
	CommonService commonService;

	public AdminChargeListVo getChargeList(AdminMileageDto adminMileageDto) {
		AdminChargeListVo adminChargeListVo = new AdminChargeListVo();
		adminChargeListVo.setList(adminMileageMapper.selectChargeList(adminMileageDto));
		adminChargeListVo.setCnt(adminMileageMapper.selectChargeListCnt(adminMileageDto));
		return adminChargeListVo;
	}

	public AdminPayListVo getPayList(AdminMileageDto adminMileageDto) {
		AdminPayListVo adminPayListVo = new AdminPayListVo();
		adminPayListVo.setList(adminMileageMapper.selectPayList(adminMileageDto));
		adminPayListVo.setCnt(adminMileageMapper.selectPayListCnt(adminMileageDto));

		return adminPayListVo;
	}

	public AdminCalculateListVo getCalculateList(AdminMileageDto adminMileageDto) {
		AdminCalculateListVo adminCalculateListVo = new AdminCalculateListVo();
		adminCalculateListVo.setList(adminMileageMapper.selectCalculateList(adminMileageDto));
		adminCalculateListVo.setCnt(adminMileageMapper.selectCalculateListCnt(adminMileageDto));

		return adminCalculateListVo;
	}

	public AdminRefundListVo getRefundList(AdminMileageDto adminMileageDto) {
		AdminRefundListVo adminRefundListVo = new AdminRefundListVo();
		adminRefundListVo.setList(adminMileageMapper.selectRefundList(adminMileageDto));
		adminRefundListVo.setCnt(adminMileageMapper.selectRefundListCnt(adminMileageDto));

		return adminRefundListVo;
	}

	public AdminDepositListVo getDepositList(AdminMileageDto adminMileageDto) {
		AdminDepositListVo adminDepositListVo = new AdminDepositListVo();
		adminDepositListVo.setList(adminMileageMapper.selectDepositList(adminMileageDto));
		adminDepositListVo.setCnt(adminMileageMapper.selectDepositListCnt(adminMileageDto));

		return adminDepositListVo;
	}

	public int putApprovalRefund(String mileageNoArr) {
		List<AdminRefundVo> list = adminMileageMapper.selectRefundAlarmList(mileageNoArr);
		
		for (int i=0; i<list.size(); i++) {
			int isAlarm = commonService.selectAlarm(list.get(i).getRegisterNo(), 5);
			if (isAlarm > 0) {
				// 2025-03-05 cjj 로그인한 사람이 국내/해외 의뢰인인지 확인.
				// 회원유형 (A:한국인, B:외국인)
				String memberTp = commonService.selectMemberTp(SecurityUtil.getMemberNo());
				String msg = "";
				if("A".equals(memberTp)){
					msg = "마일리지 환불 완료";
				}else{
					msg = "Mileage Refund complete";
				}
				// FCM
				PushDto push = new PushDto();
				push.setBody(msg + " " + list.get(i).getAmount());
				commonService.postFCMPush(list.get(i).getRegisterNo(), push, "");
				
				// 알림
				AlarmAddDto alarmAddDto = new AlarmAddDto();
				alarmAddDto.setAlarmSj(msg);
				alarmAddDto.setAlarmCn(msg + " " + list.get(i).getAmount());
				alarmAddDto.setAlarmSe("F");
				alarmAddDto.setAlarmUrl("");
				alarmAddDto.setMemberNo(list.get(i).getRegisterNo());
				commonService.postAlarm(alarmAddDto);
			}
		}
		
		int result = adminMileageMapper.updateApprovalRefund(mileageNoArr, SecurityUtil.getMemberNo());

		return result;
	}

	// 정산완료 후 알림전송
	public int putCalculateConfirm(Integer calculateNo, String stat) {
		int result = adminMileageMapper.updateCalculateConfirm(calculateNo, SecurityUtil.getMemberNo(), stat);
		if(result > 0 ){
			String calculateSe = "";
			if("Y".equals(stat)){
				calculateSe = "B";
			}else{
				calculateSe = "A";
			}
			adminMileageMapper.updateMileageCalculateConfirm(calculateNo, SecurityUtil.getMemberNo(), calculateSe);
		}
		return result;
	}

	public int putCalculateBillConfirm(Integer calculateNo) {
		return adminMileageMapper.updateMileageBillConfirm(calculateNo, SecurityUtil.getMemberNo());
	}

	public AdminCalculateGroupListVo getCalculateGroupList(AdminMileageDto adminMileageDto) {
		AdminCalculateGroupListVo adminCalculateGroupListVo = new AdminCalculateGroupListVo();
		adminCalculateGroupListVo.setList(adminMileageMapper.selectCalculateGroupList(adminMileageDto));
		adminCalculateGroupListVo.setCnt(adminMileageMapper.selectCalculateGroupListCnt(adminMileageDto));

		return adminCalculateGroupListVo;
	}

	public int putCalculateGroupConfirm(Integer calculateGroupNo) {
		List<AdminCalculateVo> list = adminMileageMapper.selectCalculateGroup(calculateGroupNo);
		int result = 0;
		if(list.size() > 0){
			for (AdminCalculateVo adminCalculateVo : list) {
				result = adminMileageMapper.updateCalculateConfirm(adminCalculateVo.getCalculateNo(), SecurityUtil.getMemberNo(), "Y");
				if(result > 0 ){
					adminMileageMapper.updateMileageCalculateConfirm(adminCalculateVo.getCalculateNo(), SecurityUtil.getMemberNo(), "B");
				}
			}
			adminMileageMapper.updateCalculateGroupConfirm(calculateGroupNo, SecurityUtil.getMemberNo());
		}

		// int isAlarm = commonService.selectAlarm(SecurityUtil.getMemberNo(), 11);
	

			// 알림톡을 보낸다.
			AlarmTalkDto alarmTalkDto = new AlarmTalkDto();
			alarmTalkDto = commonService.selectMileageInfo(SecurityUtil.getMemberNo());
			alarmTalkDto.setTemplateCode(AlarmTalkEnum.ARCHITECT_PAYMENT_PROCESSING_INITIATED.getCode());
			alarmTalkDto.setReceiverNum(alarmTalkDto.getDesignerHp());
			String content = AlarmTalkEnum.ARCHITECT_PAYMENT_PROCESSING_INITIATED.getMessageTemplate();
			String message = content.replace("#{치과기공소or치자이너}", alarmTalkDto.getDesignerNickName());
			alarmTalkDto.setContent(message);
			result = commonService.sendKaKaoSend(alarmTalkDto);

			// FCM
			PushDto push = new PushDto();
			String cn = ConstUtil.DESIGNER_OPEN_MSG8;
			String message1 = cn.replace("#{치과기공소or치자이너}", alarmTalkDto.getDesignerNickName());
			push.setBody(message1);
			commonService.postFCMPush(SecurityUtil.getMemberNo(), push, "/mileageOffice");

			// 알림
			AlarmAddDto alarmAddDto = new AlarmAddDto();
			alarmAddDto.setAlarmSj("마일리지 정산 완료");
			alarmAddDto.setAlarmCn(message1);
			alarmAddDto.setAlarmSe("F");
			alarmAddDto.setAlarmUrl("");
			alarmAddDto.setMemberNo(Integer.parseInt(alarmTalkDto.getDesignerNo()));
			commonService.postAlarm(alarmAddDto);
		

		return result;
	}
}

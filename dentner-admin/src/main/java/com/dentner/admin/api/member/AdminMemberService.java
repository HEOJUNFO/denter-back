package com.dentner.admin.api.member;


import com.dentner.admin.api.common.CommonService;
import com.dentner.core.cmmn.dto.*;
import com.dentner.core.cmmn.mapper.AdminMemberMapper;
import com.dentner.core.cmmn.service.MailService;
import com.dentner.core.cmmn.vo.MemberListVo;
import com.dentner.core.cmmn.vo.MemberVo;
import com.dentner.core.util.AlarmTalkEnum;
import com.dentner.core.util.ConstUtil;
import com.dentner.core.util.EmailUtil;
import com.dentner.core.util.SecurityUtil;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminMemberService {

	@Autowired
	AdminMemberMapper adminMemberMapper;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Resource(name= "commonService")
	CommonService commonService;

	@Autowired
	private MailService mailService;

	public MemberListVo getMemberList(MemberDto memberDto) {
		MemberListVo memberListVo = new MemberListVo();
		memberListVo.setList(adminMemberMapper.selectMemberList(memberDto));
		memberListVo.setCnt(adminMemberMapper.selectMemberListCnt(memberDto));
		
		return memberListVo;
	}

	public MemberVo getMemberDetail(int memberNo) {
		return adminMemberMapper.selectMemberDetail(memberNo);
	}

	public MemberAddDto putMember(int memberNo, MemberAddDto memberAddDto) {
		memberAddDto.setMemberNo(memberNo);
		memberAddDto.setMemberPassword(passwordEncoder.encode(memberAddDto.getMemberPassword()));
		int result = adminMemberMapper.updateMember(memberAddDto);
		return memberAddDto;
	}

	public int deleteMember(int memberNo) {
		int result = adminMemberMapper.deleteMember(memberNo);
		return memberNo;
	}

	public int putApprovalMember(String memberNo) throws Exception{
		int result = adminMemberMapper.updateApprovalMember(memberNo, SecurityUtil.getMemberNo());
		if(result > 0 ){
			AlarmTalkDto alarmTalkDto = new AlarmTalkDto();
			String[] memberArr = memberNo.split(",");
			if(memberArr.length > 0){
				for (int i = 0; i < memberArr.length; i++) {
					alarmTalkDto = adminMemberMapper.selectApprovalMember(memberArr[i]);

					if("A".equals(alarmTalkDto.getMemberSe())){	// 의뢰인 일때
						int cnt = commonService.selectAlarm(Integer.parseInt(alarmTalkDto.getMemberNo()), 14);
						if (cnt > 0) {
							if ("A".equals(alarmTalkDto.getMemberTp())) { // 국내 의뢰인인 경우 카카오톡 전송
								alarmTalkDto.setTemplateCode(AlarmTalkEnum.PROJECT_INITIATION.getCode());
								alarmTalkDto.setContent(AlarmTalkEnum.PROJECT_INITIATION.getMessageTemplate());
								result = commonService.sendKaKaoSend(alarmTalkDto);
							} else {    // 국외 의뢰인인 경우 이메일 전송
								MailDto mailDto = new MailDto();
								String emailTemplate = EmailUtil.readJoinApprovedHTMLTemplate(alarmTalkDto);

								mailDto.setMailTo(alarmTalkDto.getRequestEmail());
								mailDto.setMailSubject("[Dentner]Join Approved");
								mailDto.setMailContent(emailTemplate);
								mailService.mailSend(mailDto);
							}
						}
						// 앱알림 and push 알림
						int cnt1 = commonService.selectAlarm(Integer.parseInt(alarmTalkDto.getMemberNo()), 1);
						if (cnt1 > 0) {
							String sj = "";
							String cn = "";

							if ("A".equals(alarmTalkDto.getMemberTp())) {
								sj = "회원가입 승인";
								cn = "요청하신 회원가입이 승인 완료되었습니다.";
							}else{
								sj = "Join Approved";
								cn = "Join request approved";
							}
							// 알림 메세지 발송
							AlarmAddDto alarmAddDto = new AlarmAddDto();
							alarmAddDto.setAlarmSj(sj);
							alarmAddDto.setAlarmCn(cn);
							alarmAddDto.setAlarmSe("E");
							alarmAddDto.setAlarmUrl("");
							alarmAddDto.setMemberNo(Integer.parseInt(alarmTalkDto.getMemberNo()));
							commonService.postAlarm(alarmAddDto);

							PushDto push = new PushDto();
							push.setBody(cn);
							commonService.postFCMPush(Integer.parseInt(alarmTalkDto.getMemberNo()), push, "");
						}
					}else{ // 치자이너, 치기공 일때
						int cnt1 = commonService.selectAlarm(Integer.parseInt(alarmTalkDto.getMemberNo()), 18);
						if (cnt1 > 0) {
							alarmTalkDto.setTemplateCode(AlarmTalkEnum.PROJECT_INITIATION.getCode());
							alarmTalkDto.setContent(AlarmTalkEnum.PROJECT_INITIATION.getMessageTemplate());
							result = commonService.sendKaKaoSend(alarmTalkDto);
						}

						// 앱알림 and push 알림
						int cnt2= commonService.selectAlarm(Integer.parseInt(alarmTalkDto.getMemberNo()), 1);
						if (cnt2 > 0) {
							// 알림 메세지 발송
							AlarmAddDto alarmAddDto = new AlarmAddDto();
							alarmAddDto.setAlarmSj("회원가입 승인");
							alarmAddDto.setAlarmCn("요청하신 회원가입이 승인 완료되었습니다.");
							alarmAddDto.setAlarmSe("E");
							alarmAddDto.setAlarmUrl("");
							alarmAddDto.setMemberNo(Integer.parseInt(alarmTalkDto.getMemberNo()));
							commonService.postAlarm(alarmAddDto);

							PushDto push = new PushDto();
							push.setBody("요청하신 회원가입이 승인 완료되었습니다.");
							commonService.postFCMPush(Integer.parseInt(alarmTalkDto.getMemberNo()), push, "");
						}
					}
				}
			}
		}
		return result;
	}

	public int putApprovalRejectMember(String memberNo) throws Exception{
		int result = adminMemberMapper.updateApprovalRejectMember(memberNo, SecurityUtil.getMemberNo());
		if(result > 0 ){
			AlarmTalkDto alarmTalkDto = new AlarmTalkDto();
			String[] memberArr = memberNo.split(",");
			if(memberArr.length > 0){
				for (int i = 0; i < memberArr.length; i++) {
					alarmTalkDto = adminMemberMapper.selectApprovalMember(memberArr[i]);

					if("A".equals(alarmTalkDto.getMemberSe())){	// 의뢰인 일때
						int cnt = commonService.selectAlarm(Integer.parseInt(alarmTalkDto.getMemberNo()), 14);
						if (cnt > 0) {
							if ("A".equals(alarmTalkDto.getMemberTp())) { // 국내 의뢰인인 경우 카카오톡 전송
								alarmTalkDto.setTemplateCode(AlarmTalkEnum.PROJECT_CANCELLATION.getCode());
								alarmTalkDto.setContent(AlarmTalkEnum.PROJECT_CANCELLATION.getMessageTemplate());
								result = commonService.sendKaKaoSend(alarmTalkDto);
							} else {    // 국외 의뢰인인 경우 이메일 전송
								MailDto mailDto = new MailDto();
								alarmTalkDto.setMemberNickName(commonService.getMemberNickName(String.valueOf(memberArr[i])));
								String emailTemplate = EmailUtil.readJoinDeniedHTMLTemplate(alarmTalkDto);

								mailDto.setMailTo(alarmTalkDto.getRequestEmail());
								mailDto.setMailSubject("[Dentner]Join Denied");
								mailDto.setMailContent(emailTemplate);
								mailService.mailSend(mailDto);
							}
						}

						// 앱알림 or push 알림
						int cnt1 = commonService.selectAlarm(Integer.parseInt(alarmTalkDto.getRegisterNo()), 1);
						if (cnt1 > 0) {

						}
					}else{ // 치자이너, 치기공 일때
						int cnt1 = commonService.selectAlarm(Integer.parseInt(alarmTalkDto.getMemberNo()), 18);
						if (cnt1 > 0) {
							alarmTalkDto.setTemplateCode(AlarmTalkEnum.PROJECT_CANCELLATION.getCode());
							alarmTalkDto.setContent(AlarmTalkEnum.PROJECT_CANCELLATION.getMessageTemplate());
							result = commonService.sendKaKaoSend(alarmTalkDto);
						}

						// 앱알림 or push 알림 추후에 앱 보내려면 내용 추가하면 됨.
						int cnt2 = commonService.selectAlarm(Integer.parseInt(alarmTalkDto.getRegisterNo()), 1);
						if (cnt2 > 0) {

						}
					}

					//adminMemberMapper.deleteRealMember(memberArr[i]);
				}
			}
		}
		return result;
	}

	public int putOutApprovalMember(int memberNo) {
		
		adminMemberMapper.updateMemberOut(memberNo, SecurityUtil.getMemberNo());
		
		return adminMemberMapper.updateOutApprovalMember(memberNo, SecurityUtil.getMemberNo());
	}

	public List<MemberVo> getMemberExcelList(MemberDto memberDto) {
		return adminMemberMapper.selectMemberExcelList(memberDto);
	}
}

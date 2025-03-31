package com.dentner.front.config;

import com.dentner.core.cmmn.dto.AlarmAddDto;
import com.dentner.core.cmmn.dto.AlarmTalkDto;
import com.dentner.core.cmmn.dto.MailDto;
import com.dentner.core.cmmn.mapper.FrontTransactionMapper;
import com.dentner.core.cmmn.service.MailService;
import com.dentner.core.cmmn.vo.RequestTaskVo;
import com.dentner.core.util.AlarmTalkEnum;
import com.dentner.core.util.ConstUtil;
import com.dentner.core.util.EmailUtil;
import com.dentner.core.util.SecurityUtil;
import com.dentner.front.api.common.CommonService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScheduledTasksConfig {
    @Autowired
    FrontTransactionMapper frontTransactionMapper;

    @Resource(name= "commonService")
    CommonService commonService;

    @Autowired
    private MailService mailService;

    @Scheduled(cron = "0 0/30 * * * *")  // 매 시각 00분과 30분에 실행
    public void performTask() {
        // 1. 견적 요청 만료일이 도래된 건들 확인
        List<RequestTaskVo> requestTaskVoList = frontTransactionMapper.selectTransactionTaskList();
        if(requestTaskVoList != null && requestTaskVoList.size() > 0){
            // 2. 견적 요청 건수에 따른 상태값을 변경해 준다.
            for (int i = 0; i < requestTaskVoList.size(); i++) {
                RequestTaskVo requestTaskVo = requestTaskVoList.get(i);
                // 2.1 견적이 하나도 없을 경우 요청마감 G
                if(requestTaskVo.getEstimateCnt() == 0){
                    frontTransactionMapper.updateRequestStatus(requestTaskVo.getRequestFormNo(), "G", 0);

                    // 알림톡을 보낸다.
                    AlarmTalkDto alarmTalkDto = new AlarmTalkDto();
                    alarmTalkDto = commonService.selectRequestInfo(requestTaskVo.getRequestFormNo());
                    alarmTalkDto.setTemplateCode(AlarmTalkEnum.PROJECT_COMPLETION_CONFIRMATION.getCode());
                    alarmTalkDto.setReceiverNum(alarmTalkDto.getRequestHp());
                    String content = AlarmTalkEnum.PROJECT_COMPLETION_CONFIRMATION.getMessageTemplate();
                    String message = content.replace("#{프로젝트}", alarmTalkDto.getRequestFormSj());
                    alarmTalkDto.setContent(message);
                    commonService.sendKaKaoSend(alarmTalkDto);

                    // 2025-03-05 cjj 로그인한 사람이 국내/해외 의뢰인인지 확인.
                    // 회원유형 (A:한국인, B:외국인)
                    String memberTp = commonService.selectMemberTp(SecurityUtil.getMemberNo());
                    String sj = "";
                    String cn = "";
                    if("A".equals(memberTp)){
                        sj = "견적 요청 만료";
                        cn = ConstUtil.REQUEST_OPEN_MSG10;
                    }else{
                        sj = "Quote red expired";
                        cn = ConstUtil.REQUEST_OPEN_ENG_MSG10;
                    }

                    AlarmAddDto alarmAddDto = new AlarmAddDto();
                    alarmAddDto.setAlarmSj(sj);
                    String message1 = cn;
                    alarmAddDto.setAlarmCn(message1);
                    alarmAddDto.setAlarmSe("D");
                    alarmAddDto.setAlarmUrl(requestTaskVo.getRequestFormNo().toString());
                    alarmAddDto.setMemberNo(Integer.parseInt(alarmTalkDto.getRegisterNo()));
                    commonService.postAlarm(alarmAddDto);

                }else{
                    // 2.2 견적이 있을 경우 치자이너 선택 B
                    frontTransactionMapper.updateRequestStatus(requestTaskVo.getRequestFormNo(), "B", 0);
                }
            }
        }
    }

    @Scheduled(cron = "0 0/10 * * * *")  // 매 시각 00분과10분에 실행
    public void deadlineTask() throws Exception{
        // 1. 납품 마감 만료일이 도래된 건들 확인
        List<RequestTaskVo> requestTaskVoList = frontTransactionMapper.selectTransactionDeadlineList();
        if(requestTaskVoList != null && requestTaskVoList.size() > 0){
            // 2. 납품 마감에 따른 납품여부 값을 변경.
            for (int i = 0; i < requestTaskVoList.size(); i++) {
                RequestTaskVo requestTaskVo = requestTaskVoList.get(i);
                // 알림톡을 보낸다.
                AlarmTalkDto alarmTalkDto = new AlarmTalkDto();
                alarmTalkDto = commonService.selectRequestInfo(requestTaskVo.getRequestFormNo());

                int cnt = commonService.selectAlarm(Integer.parseInt(alarmTalkDto.getRegisterNo()), 17);
                if (cnt > 0) { // 알림이 켜져 있음.
                    if("A".equals(alarmTalkDto.getMemberTp())) {    // 국내 의뢰인인 경우 카카오톡 전송
                        alarmTalkDto.setTemplateCode(AlarmTalkEnum.PROJECT_COMPLETION_AND_PAYMENT_PROCESSING.getCode());
                        alarmTalkDto.setReceiverNum(alarmTalkDto.getRequestHp());
                        String content = AlarmTalkEnum.PROJECT_COMPLETION_AND_PAYMENT_PROCESSING.getMessageTemplate();
                        String message = content.replace("#{치자이너}", alarmTalkDto.getDesignerNickName())
                                .replace("#{프로젝트}", alarmTalkDto.getRequestFormSj());
                        alarmTalkDto.setContent(message);
                        commonService.sendKaKaoSend(alarmTalkDto);
                    }else{
                        // 이메일 발송
                        MailDto mailDto = new MailDto();
                        String emailTemplate = EmailUtil.readDeadlineHTMLTemplate(alarmTalkDto);

                        mailDto.setMailTo(alarmTalkDto.getRequestEmail());
                        mailDto.setMailSubject("[Dentner]Deadline passed");
                        mailDto.setMailContent(emailTemplate);
                        mailService.mailSend(mailDto);
                    }
                }

                if("B".equals(alarmTalkDto.getRequestFormSe())){   // 지정 요청
                    int cnt1 = commonService.selectAlarm(Integer.parseInt(alarmTalkDto.getRegisterNo()), 4);
                    if (cnt1 > 0) {
                        // 2025-03-05 cjj 로그인한 사람이 국내/해외 의뢰인인지 확인.
                        // 회원유형 (A:한국인, B:외국인)
                        String memberTp = requestTaskVo.getMemberTp();
                        String sj = "";
                        String cn = "";
                        if("A".equals(memberTp)){
                            sj = "납품 마감";
                            cn = ConstUtil.REQUEST_TARGET_MSG3;
                        }else{
                            sj = "Deadline";
                            cn = ConstUtil.REQUEST_TARGET_ENG_MSG3;
                        }
                        // 납품마감이 지났을 경우 알림 보내기
                        AlarmAddDto alarmAddDto = new AlarmAddDto();
                        alarmAddDto.setAlarmSj(sj);
                        String message1 = cn;
                        alarmAddDto.setAlarmCn(message1);
                        alarmAddDto.setAlarmSe("D");
                        alarmAddDto.setAlarmUrl(requestTaskVo.getRequestFormNo().toString());
                        alarmAddDto.setMemberNo(Integer.parseInt(alarmTalkDto.getRegisterNo()));
                        commonService.postAlarm(alarmAddDto);
                    }
                }

                frontTransactionMapper.updateRequestDeadline(requestTaskVo.getRequestFormNo(), 0);
            }
        }
    }
}

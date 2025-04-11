package com.dentner.front.api.transaction;

import com.dentner.core.cmmn.dto.*;
import com.dentner.core.cmmn.mapper.ChatMapper;
import com.dentner.core.cmmn.mapper.FileMapper;
import com.dentner.core.cmmn.mapper.FrontRequestMapper;
import com.dentner.core.cmmn.mapper.FrontTransactionMapper;
import com.dentner.core.cmmn.service.MailService;
import com.dentner.core.cmmn.service.S3Upload;
import com.dentner.core.cmmn.vo.*;
import com.dentner.core.util.*;
import com.dentner.front.api.common.CommonService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransactionService {

	@Autowired
	FrontTransactionMapper frontTransactionMapper;

	@Autowired
	FrontRequestMapper frontRequestMapper;

	@Autowired
	FileMapper fileMapper;

	private final S3Upload s3Upload;

	@Resource(name= "commonService")
	CommonService commonService;

	@Autowired
	ChatMapper chatMapper;

	@Autowired
	private MailService mailService;

	public RequestFormListVo getTransactionFormList(HttpServletRequest request, String requestFormSe, RequestFormDto requestFormDto) {
		String language = request.getHeader("language");
		if(language != null && !"".equals(language)){
			requestFormDto.setLanguage(language);
		}

		requestFormDto.setRequestFormSe(requestFormSe);
		requestFormDto.setRegisterNo(SecurityUtil.getMemberNo());
		requestFormDto.setMemberSe(SecurityUtil.getMemberSe());

		RequestFormListVo requestFormListVo = new RequestFormListVo();
		List<RequestFormVo> requestFormList = new ArrayList<RequestFormVo>();
		int requestFormCnt = 0;

		if("A".equals(SecurityUtil.getMemberSe())){	// 의뢰인
			requestFormList = frontTransactionMapper.selectTransactionList(requestFormDto);
			requestFormCnt = frontTransactionMapper.selectTransactionListCnt(requestFormDto);
		}else{	// 치자이너
			requestFormList = frontTransactionMapper.selectTransactionDesignerList(requestFormDto);
			requestFormCnt = frontTransactionMapper.selectTransactionDesignerListCnt(requestFormDto);
		}
		requestFormListVo.setList(requestFormList);
		requestFormListVo.setCnt(requestFormCnt);

		return requestFormListVo;
	}

	public RequestEstimateListVo getTransactionEstimateList(Integer requestFormNo, String estimateSe, RequestEstimateDto requestEstimateDto) {
		requestEstimateDto.setRequestFormNo(requestFormNo);
		requestEstimateDto.setRegisterNo(SecurityUtil.getMemberNo());
		requestEstimateDto.setEstimateSe(estimateSe);

		RequestEstimateListVo requestEstimateListVo = new RequestEstimateListVo();
		// 2024.09-19 공개요청인지 지정요청인지 확인.
		TransactionPaymentVo transactionPaymentVo = frontTransactionMapper.selectTransactionPayment(requestFormNo, SecurityUtil.getMemberNo());
		if(transactionPaymentVo != null){
			if("A".equals(transactionPaymentVo.getRequestFormSe())){	// 공개 요청
				requestEstimateListVo.setList(frontTransactionMapper.selectTransactionEstimateList(requestEstimateDto));
				requestEstimateListVo.setCnt(frontTransactionMapper.selectTransactionEstimateListCnt(requestEstimateDto));
			}else{	// 지정요청
				requestEstimateListVo.setList(frontTransactionMapper.selectTransactionTargetList(requestEstimateDto));
			}
		}
		return requestEstimateListVo;
	}

	public int postEstimateChoice(Integer requestFormNo, Integer targetNo) {
		int result = 0;
		result = frontTransactionMapper.updateEstimateAllCancel(requestFormNo);
		if(result > 0 ){
			result = frontTransactionMapper.updateEstimateChoice(requestFormNo, targetNo);
			// 공개요청서에서 치자이너 선택 후 상태값 변경 및 치자이너 no 변경
			frontTransactionMapper.updateRequestStatus(requestFormNo, "C", SecurityUtil.getMemberNo());
			frontTransactionMapper.updateDesignerNo(requestFormNo, targetNo, SecurityUtil.getMemberNo());
			frontTransactionMapper.updateRequestDealStatus(requestFormNo, "B", SecurityUtil.getMemberNo());

			// 2024-10-16 선택되지 않은 치자이너들 상태 업데이트
			frontTransactionMapper.updateDesignerStatus(requestFormNo);

			int isAlarm = commonService.selectAlarm(targetNo, 10);
            if (isAlarm > 0) {
            	String url = ConstUtil.DESIGNER_ALARM3_URL.replace("{REQUEST_FORM_NO}", requestFormNo.toString());

				// 2025-03-05 cjj 로그인한 사람이 국내/해외 의뢰인인지 확인.
				// 회원유형 (A:한국인, B:외국인)
				String memberTp = commonService.selectMemberTp(SecurityUtil.getMemberNo());
				String msg = "";
				String cn = "";
				if("A".equals(memberTp)){
					msg = "치자이너 선택";
					cn = "의뢰인이 치자이너님을 선택하였습니다.";
				}else{
					msg = "T-esigner Choose";
					cn = "You were chosen by the client.";
				}

            	// 알림 메세지 추가
            	AlarmAddDto alarmAddDto = new AlarmAddDto();
            	alarmAddDto.setAlarmSj(msg);
            	alarmAddDto.setAlarmCn(cn);
            	alarmAddDto.setAlarmSe("B");
            	alarmAddDto.setAlarmUrl(url);
            	alarmAddDto.setMemberNo(targetNo);
            	commonService.postAlarm(alarmAddDto);
            	
            	// FCM
            	PushDto push = new PushDto();
                push.setBody(cn);
                commonService.postFCMPush(targetNo, push, url);
            	
            	// 알림톡을 보낸다.
            	AlarmTalkDto alarmTalkDto = new AlarmTalkDto();
            	alarmTalkDto = commonService.selectRequestInfo(requestFormNo);
            	alarmTalkDto.setTemplateCode(AlarmTalkEnum.ARCHITECT_PROPOSAL_SUBMISSION.getCode());
            	alarmTalkDto.setReceiverNum(alarmTalkDto.getDesignerHp());
            	String content = AlarmTalkEnum.ARCHITECT_PROPOSAL_SUBMISSION.getMessageTemplate();
            	String message = content.replace("#{의뢰인}", alarmTalkDto.getRequestNickName())
            			.replace("#{project_name}", alarmTalkDto.getRequestFormSj());
            	alarmTalkDto.setContent(message);
            	result = commonService.sendKaKaoSend(alarmTalkDto);
            }
			
		}

		return result;
	}

	public int putRequestStatus(Integer requestFormNo, String status) {
		int result = 0;
		result = frontTransactionMapper.updateRequestStatus(requestFormNo, status, SecurityUtil.getMemberNo());
		if("D".equals(status)){	// 거래 완료 일때
			AlarmTalkDto alarmTalkDto = new AlarmTalkDto();
			alarmTalkDto = commonService.selectRequestInfo(requestFormNo);

			// 2025-03-05 cjj 로그인한 사람이 국내/해외 의뢰인인지 확인.
			// 회원유형 (A:한국인, B:외국인)
			String memberTp = commonService.selectMemberTp(SecurityUtil.getMemberNo());
			String sj = "";
			String cn = "";
			if("A".equals(memberTp)){
				sj = "거래 완료";
				cn = ConstUtil.REQUEST_OPEN_MSG7;
			}else{
				sj = "completed Txn";
				cn = ConstUtil.REQUEST_OPEN_ENG_MSG7;
			}

			// 알림 메세지 발송 - 의뢰인에게 발송
			AlarmAddDto alarmAddDto = new AlarmAddDto();
			alarmAddDto.setAlarmSj(sj);
			String message = cn;
			alarmAddDto.setAlarmCn(message);
			alarmAddDto.setAlarmSe("D");
			alarmAddDto.setAlarmUrl(requestFormNo.toString());
			alarmAddDto.setMemberNo(SecurityUtil.getMemberNo());
			
			int isAlarm = commonService.selectAlarm(SecurityUtil.getMemberNo(), 4);
            if (isAlarm > 0) {
            	
            	commonService.postAlarm(alarmAddDto);
            	
            	// FCM
            	PushDto push = new PushDto();
                push.setBody(message);
                commonService.postFCMPush(SecurityUtil.getMemberNo(), push, "/payment");
            }
			
            // 거래완료
            int isAlarm2 = commonService.selectAlarm(Integer.parseInt(alarmTalkDto.getDesignerNo()), 10);
            if (isAlarm2 > 0) {
            	String message1 = ConstUtil.DESIGNER_OPEN_MSG4;
            	// FCM
            	PushDto push = new PushDto();
                push.setBody(message1);
                commonService.postFCMPush(Integer.parseInt(alarmTalkDto.getDesignerNo()), push, "/payment");
            	
            	// 치자이너에게 한번 더 발송
				alarmAddDto.setAlarmSj("거래완료");
                alarmAddDto.setAlarmCn(message1);
            	alarmAddDto.setMemberNo(Integer.parseInt(alarmTalkDto.getDesignerNo()));
            	commonService.postAlarm(alarmAddDto);
            }
            
            // 마일리지 입금
            int isAlarm3 = commonService.selectAlarm(Integer.parseInt(alarmTalkDto.getDesignerNo()), 11);
            if (isAlarm3 > 0) {
            	PushDto push = new PushDto();
            	String msg = "마일리지 입금 " + alarmTalkDto.getAmount() +"/" + alarmTalkDto.getRequestNickName();
            	
                push.setBody(msg);
                //commonService.postFCMPush(Integer.parseInt(alarmTalkDto.getDesignerNo()), push, "/mileageOffice");

				alarmAddDto.setAlarmSj("마일리지 입금");
                alarmAddDto.setAlarmCn(msg);
            	alarmAddDto.setMemberNo(Integer.parseInt(alarmTalkDto.getDesignerNo()));
            	//commonService.postAlarm(alarmAddDto);
            }

		}
		return result;
	}

	public int putRequestDealStatus(Integer requestFormNo, String status) {
		int result = 0;
		result = frontTransactionMapper.updateRequestDealStatus(requestFormNo, status, SecurityUtil.getMemberNo());
		return result;
	}
	public TransactionEstimateDetailVo getTransactionEstimate(Integer requestEstimateNo) {
		TransactionEstimateDetailVo transactionEstimateDetailVo = new TransactionEstimateDetailVo();
		transactionEstimateDetailVo = frontTransactionMapper.selectTransactionEstimateDetail(requestEstimateNo, SecurityUtil.getMemberNo());

		if(transactionEstimateDetailVo != null){
			if(!"B".equals(transactionEstimateDetailVo.getRequestFormSe())){
				transactionEstimateDetailVo.setProstheticsList(frontTransactionMapper.selectEstimateProstheticsList(requestEstimateNo));
				transactionEstimateDetailVo.setRequestDocDesc(frontTransactionMapper.selectEstimateDocDesc(requestEstimateNo));
				transactionEstimateDetailVo.setImageList(fileMapper.selectFileList(transactionEstimateDetailVo.getMemberNo(), "F"));
			}else{
				return null;
			}
		}else{
			return null;
		}

		return transactionEstimateDetailVo;
	}

	public TransactionPaymentVo getTransactionPayment(Integer requestFormNo) {
		TransactionPaymentVo transactionPaymentVo = new TransactionPaymentVo();
		transactionPaymentVo = frontTransactionMapper.selectTransactionPayment(requestFormNo, SecurityUtil.getMemberNo());

		if(transactionPaymentVo != null){
			//if(!"B".equals(transactionPaymentVo.getRequestFormSe())){

			//}else{
			//	return null;
			//}
			if(!"B".equals(transactionPaymentVo.getRequestFormSe())){
				transactionPaymentVo.setProstheticsList(frontTransactionMapper.selectPaymentProstheticsList(requestFormNo));
				transactionPaymentVo.setRequestDocDesc(frontTransactionMapper.selectPaymentDocDesc(requestFormNo));
			}else{
				List<HashMap<String, Object>> prostheticsList = frontRequestMapper.selectRequestDetailProstheticsList(requestFormNo);
				if(prostheticsList != null && prostheticsList.size() > 0){
					int targetAmount = 0;
					for (int i = 0; i < prostheticsList.size(); i++) {
						targetAmount += (Integer.parseInt(prostheticsList.get(i).get("amount").toString()) * Integer.parseInt(prostheticsList.get(i).get("count").toString()));
					}
					transactionPaymentVo.setTargetAmount(targetAmount);
				}
				transactionPaymentVo.setProstheticsList(prostheticsList);

				transactionPaymentVo.setRequestDocDesc(frontTransactionMapper.selectPaymentDocDesc(requestFormNo));
			}
		}

		return transactionPaymentVo;

	}

	public List<Map<String, Object>> getTransactionDoc(Integer requestFormNo) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		list = frontTransactionMapper.selectTransactionDoc(requestFormNo, SecurityUtil.getMemberNo());
		if(list.size() > 0){
			for (int i = 0; i < list.size(); i++) {
				list.get(i).put("requestDocDesc", frontTransactionMapper.selectPaymentDocDesc(requestFormNo));
			}
		}
		return list;
	}

	public List<RequestDocVo> getTransactionDocDetail(Integer requestDocGroupNo) {
		List<RequestDocVo> requestDocVoList = new ArrayList<RequestDocVo>();
		RequestDocVo requestDocVo = null;
		String memberSe = SecurityUtil.getMemberSe();

		requestDocVoList = frontTransactionMapper.selectTransactionDocDetail(requestDocGroupNo, SecurityUtil.getMemberNo(), memberSe);
		if(requestDocVoList != null){
			if(requestDocVoList.size() > 0){
				for (int i = 0; i < requestDocVoList.size(); i++) {
					requestDocVo = requestDocVoList.get(i);
					requestDocVo.setProstheticsList(frontTransactionMapper.selectDocProstheticsList(requestDocVo.getRequestDocNo()));
					requestDocVo.setFileList(fileMapper.selectFileList(requestDocGroupNo, "G"));
				}
			}
		}

		return requestDocVoList;
	}

    public int postTransactionCad(Integer requestFormNo, TransactionCadAddDto transactionCadAddDto, List<MultipartFile> cadFiles, List<MultipartFile> files) throws Exception{
		transactionCadAddDto.setRequestFormNo(requestFormNo);
		transactionCadAddDto.setRegisterNo(SecurityUtil.getMemberNo());

		int result = 0;
		// cad 파일 업로드
		if(!"".equals(transactionCadAddDto.getDocList())){
			ObjectMapper objectMapper = new ObjectMapper();
			List<TransactionCadFileDto> docList;

			try {
				String jsonString = transactionCadAddDto.getDocList();
				docList = objectMapper.readValue(jsonString, new TypeReference<List<TransactionCadFileDto>>(){});
				for (TransactionCadFileDto doc : docList) {
					String fileName = doc.getFileName();
					MultipartFile file = findFileByName(cadFiles, fileName);
					if (file != null) {
						S3FileVO fileVO = s3Upload.uploadToOrgName(file, doc.getFileOrgName());
						fileVO.setFileFromNo(doc.getRequestDocGroupNo());
						fileVO.setFileSe("J");
						fileVO.setRegisterNo(SecurityUtil.getMemberNo());
						result =+ fileMapper.insertFile(fileVO);
					} 
				}
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}

		AlarmTalkDto alarmTalkDto = new AlarmTalkDto();
		alarmTalkDto = commonService.selectRequestInfo(requestFormNo);
		alarmTalkDto.setReceiverNum(alarmTalkDto.getRequestHp());
		String content = "";

		// 2025-03-05 cjj 로그인한 사람이 국내/해외 의뢰인인지 확인.
		// 회원유형 (A:한국인, B:외국인)
		String memberTp = commonService.selectMemberTp(Integer.parseInt(alarmTalkDto.getRegisterNo()));
		String sj = "";
		if("A".equals(memberTp)){
			sj = "CAD파일 업로드";
		}else{
			sj = "CAD file uploaded";
		}

		AlarmAddDto alarmAddDto = new AlarmAddDto();
		alarmAddDto.setAlarmSj(sj);

		// 의뢰인 카카오 or 이메일 알림 여부
		int cnt = commonService.selectAlarm(Integer.parseInt(alarmTalkDto.getRegisterNo()), 17);
		// 의뢰인 앱 알림 여부
		int cnt1 = commonService.selectAlarm(Integer.parseInt(alarmTalkDto.getRegisterNo()), 4);

		// 추가금이 있을때만
		if("Y".equals(transactionCadAddDto.getRequestPaySe())){
			result =+ frontTransactionMapper.insertTransactionAddPay(transactionCadAddDto);

			if(files != null){
				for (int i = 0; i < files.size(); i++) {
					S3FileVO fileVO = s3Upload.upload(files.get(i));
					fileVO.setFileFromNo(transactionCadAddDto.getRequestFormPayNo());
					fileVO.setFileSe("I");
					fileVO.setRegisterNo(SecurityUtil.getMemberNo());
					result =+ fileMapper.insertFile(fileVO);
				}
			}

			// 2024.09.23 CJJ 재제작 테이블에 추가금 결제 NO 추가
			frontTransactionMapper.updateRemakingAddPay(requestFormNo, transactionCadAddDto.getRequestFormPayNo());

			result = frontTransactionMapper.updateRequestDealStatus(requestFormNo, "F", SecurityUtil.getMemberNo());

			String url = ConstUtil.REQUEST_ALARM1_URL.replace("{REQUEST_FORM_NO}", requestFormNo.toString());
			
			// 알림톡을 보낸다. 추가금이 있을때
			if("N".equals(transactionCadAddDto.getIsRework())){	// 재제작이 아닐때
				if (cnt > 0) {    // 카카오 or 이메일 알림이 켜져 있음.
					if("A".equals(memberTp)) {    // 국내 의뢰인인 경우 카카오톡 전송
						alarmTalkDto.setTemplateCode(AlarmTalkEnum.PROJECT_PROGRESS_REPORT_REVISION.getCode());
						content = AlarmTalkEnum.PROJECT_PROGRESS_REPORT_REVISION.getMessageTemplate();

						String message = content.replace("#{치자이너}", alarmTalkDto.getDesignerNickName())
								.replace("#{project_name}", alarmTalkDto.getRequestFormSj());
						alarmTalkDto.setContent(message);

						result = commonService.sendKaKaoSend(alarmTalkDto);
					}else{
						// 이메일 발송
						MailDto mailDto = new MailDto();
						String emailTemplate = EmailUtil.readCadUploadSurchargeHTMLTemplate(alarmTalkDto);

						// template 변경
						mailDto.setMailTo(alarmTalkDto.getRequestEmail());
						mailDto.setMailSubject("[Dentner]CAD file uploaded");
						mailDto.setMailContent(emailTemplate);
						mailService.mailSend(mailDto);
					}
				}

				if (cnt1 > 0) { // 앱 알림이 켜져 있음.
					String cn = "";
					if("A".equals(memberTp)){
						cn = ConstUtil.REQUEST_OPEN_MSG12;
					}else{
						cn = ConstUtil.REQUEST_OPEN_ENG_MSG12;
					}
					// 알림보내기.
					String message = cn.replace("#{치자이너}", alarmTalkDto.getDesignerNickName())
							.replace("#{요청서명}", alarmTalkDto.getRequestFormSj());
					alarmAddDto.setAlarmCn(message);
					alarmAddDto.setAlarmSe("D");
					alarmAddDto.setAlarmUrl(url);
					alarmAddDto.setMemberNo(Integer.parseInt(alarmTalkDto.getRegisterNo()));

					commonService.postAlarm(alarmAddDto);

					PushDto push = new PushDto();
					push.setBody(message);
					commonService.postFCMPush(Integer.parseInt(alarmTalkDto.getRegisterNo()), push, url);
				}
			}else{ // 재제작일때
				if (cnt > 0) {    // 카카오 or 이메일 알림이 켜져 있음.
					if("A".equals(memberTp)) {    // 국내 의뢰인인 경우 카카오톡 전송
						alarmTalkDto.setTemplateCode(AlarmTalkEnum.PROJECT_PROGRESS_REPORT_REVISION_WITH_ADDITIONAL_PAYMENT.getCode());
						content = AlarmTalkEnum.PROJECT_PROGRESS_REPORT_REVISION_WITH_ADDITIONAL_PAYMENT.getMessageTemplate();

						String message = content.replace("#{치자이너}", alarmTalkDto.getDesignerNickName())
								.replace("#{project_name}", alarmTalkDto.getRequestFormSj());
						alarmTalkDto.setContent(message);

						result = commonService.sendKaKaoSend(alarmTalkDto);
					}else{
						// 이메일 발송
						MailDto mailDto = new MailDto();
						String emailTemplate = EmailUtil.readCadRemakeSurchargeHTMLTemplate(alarmTalkDto);

						// template 변경
						mailDto.setMailTo(alarmTalkDto.getRequestEmail());
						mailDto.setMailSubject("[Dentner]CAD file uploaded");
						mailDto.setMailContent(emailTemplate);
						mailService.mailSend(mailDto);
					}
				}

				if (cnt1 > 0) { // 앱 알림이 켜져 있음.
					String cn = "";
					if("A".equals(memberTp)){
						cn = ConstUtil.REQUEST_OPEN_MSG13;
					}else{
						cn = ConstUtil.REQUEST_OPEN_ENG_MSG13;
					}
					// 알림보내기.
					String message = cn.replace("#{치자이너}", alarmTalkDto.getDesignerNickName())
							.replace("#{요청서명}", alarmTalkDto.getRequestFormSj());
					alarmAddDto.setAlarmCn(message);
					alarmAddDto.setAlarmSe("D");
					alarmAddDto.setAlarmUrl(url);
					alarmAddDto.setMemberNo(Integer.parseInt(alarmTalkDto.getRegisterNo()));

					commonService.postAlarm(alarmAddDto);

					PushDto push = new PushDto();
					push.setBody(message);
					commonService.postFCMPush(Integer.parseInt(alarmTalkDto.getRegisterNo()), push, url);
				}
			}
		}else{ // 추가금이 없을때
			result = frontTransactionMapper.updateRequestDealStatus(requestFormNo, "G", SecurityUtil.getMemberNo());

			String url = ConstUtil.REQUEST_ALARM2_URL.replace("{REQUEST_FORM_NO}", requestFormNo.toString());
			
			// 알림톡을 보낸다. 추가금이 없을때
			if("N".equals(transactionCadAddDto.getIsRework())) {    // 재제작이 아닐때
				if (cnt > 0) {    // 카카오 or 이메일 알림이 켜져 있음.
					if("A".equals(memberTp)) {    // 국내 의뢰인인 경우 카카오톡 전송
						alarmTalkDto.setTemplateCode(AlarmTalkEnum.PROJECT_PROGRESS_REPORT_COMPLETION.getCode());
						content = AlarmTalkEnum.PROJECT_PROGRESS_REPORT_COMPLETION.getMessageTemplate();

						String message = content.replace("#{치자이너}", alarmTalkDto.getDesignerNickName())
								.replace("#{project_name}", alarmTalkDto.getRequestFormSj());
						alarmTalkDto.setContent(message);

						result = commonService.sendKaKaoSend(alarmTalkDto);
					}else{
						// 이메일 발송
						MailDto mailDto = new MailDto();
						String emailTemplate = EmailUtil.readCadUploadHTMLTemplate(alarmTalkDto);

						// template 변경
						mailDto.setMailTo(alarmTalkDto.getRequestEmail());
						mailDto.setMailSubject("[Dentner]CAD file uploaded");
						mailDto.setMailContent(emailTemplate);
						mailService.mailSend(mailDto);
					}
				}

				if (cnt1 > 0) { // 앱 알림이 켜져 있음.
					// 알림보내기.
					String cn = "";
					if("A".equals(memberTp)){
						cn = ConstUtil.REQUEST_OPEN_MSG21;
					}else{
						cn = ConstUtil.REQUEST_OPEN_ENG_MSG21;
					}

					// 알림보내기.
					String message = cn.replace("#{치자이너}", alarmTalkDto.getDesignerNickName())
							.replace("#{요청서명}", alarmTalkDto.getRequestFormSj());
					alarmAddDto.setAlarmCn(message);
					alarmAddDto.setAlarmSe("D");
					alarmAddDto.setAlarmUrl(url);
					alarmAddDto.setMemberNo(Integer.parseInt(alarmTalkDto.getRegisterNo()));

					commonService.postAlarm(alarmAddDto);

					PushDto push = new PushDto();
					push.setBody(message);
					commonService.postFCMPush(Integer.parseInt(alarmTalkDto.getRegisterNo()), push, url);
				}

			}else{
				if (cnt > 0) {    // 카카오 or 이메일 알림이 켜져 있음.
					if("A".equals(memberTp)) {    // 국내 의뢰인인 경우 카카오톡 전송
						alarmTalkDto.setTemplateCode(AlarmTalkEnum.PROJECT_PROGRESS_REPORT_SUBMISSION.getCode());
						content = AlarmTalkEnum.PROJECT_PROGRESS_REPORT_SUBMISSION.getMessageTemplate();

						String message = content.replace("#{치자이너}", alarmTalkDto.getDesignerNickName())
								.replace("#{project_name}", alarmTalkDto.getRequestFormSj());
						alarmTalkDto.setContent(message);

						result = commonService.sendKaKaoSend(alarmTalkDto);
					}else{
						// 이메일 발송
						MailDto mailDto = new MailDto();
						String emailTemplate = EmailUtil.readCadRemakeHTMLTemplate(alarmTalkDto);

						// template 변경
						mailDto.setMailTo(alarmTalkDto.getRequestEmail());
						mailDto.setMailSubject("[Dentner]CAD file uploaded");
						mailDto.setMailContent(emailTemplate);
						mailService.mailSend(mailDto);
					}
				}

				if (cnt1 > 0) { // 앱 알림이 켜져 있음.
					String cn = "";
					if("A".equals(memberTp)){
						cn = ConstUtil.REQUEST_OPEN_MSG14;
					}else{
						cn = ConstUtil.REQUEST_OPEN_ENG_MSG14;
					}

					String message = cn.replace("{치자이너 닉네임}", alarmTalkDto.getDesignerNickName());
					alarmAddDto.setAlarmCn(message);
					alarmAddDto.setAlarmSe("D");
					alarmAddDto.setAlarmUrl(url);
					alarmAddDto.setMemberNo(Integer.parseInt(alarmTalkDto.getRegisterNo()));

					commonService.postAlarm(alarmAddDto);
					PushDto push = new PushDto();
					push.setBody(message);
					commonService.postFCMPush(Integer.parseInt(alarmTalkDto.getRegisterNo()), push, url);
				}
			}
		}

		return result;
    }

	private MultipartFile findFileByName(List<MultipartFile> files, String fileName) {

		System.out.println("fileName = " + fileName);
		return files.stream()
				.filter(file -> file.getOriginalFilename().equals(fileName))
				.findFirst()
				.orElse(null);
	}

	public TransactionEstimateDetailVo getTransactionEstimateDesigner(Integer requestFormNo) {
		TransactionEstimateDetailVo transactionEstimateDetailVo = new TransactionEstimateDetailVo();
		transactionEstimateDetailVo = frontTransactionMapper.selectTransactionEstimateDetailDesigner(requestFormNo, SecurityUtil.getMemberNo());
		if(transactionEstimateDetailVo != null){
			if(!"B".equals(transactionEstimateDetailVo.getRequestFormSe())){
				transactionEstimateDetailVo.setProstheticsList(frontTransactionMapper.selectEstimateDesignerProstheticsList(requestFormNo, SecurityUtil.getMemberNo()));
				transactionEstimateDetailVo.setRequestDocDesc(frontTransactionMapper.selectEstimateDesignerDocDesc(requestFormNo, SecurityUtil.getMemberNo()));
				transactionEstimateDetailVo.setImageList(fileMapper.selectFileList(transactionEstimateDetailVo.getMemberNo(), "F"));
				List<S3FileListVO> fileListVO = fileMapper.selectFileLists(SecurityUtil.getMemberNo(), "O");
				if(fileListVO.size() > 0){
					for (int i = 0; i < fileListVO.size(); i++) {
						fileListVO.get(i).setCadList(fileMapper.selectFileCadList(SecurityUtil.getMemberNo(), "P", fileListVO.get(i).getFileOrdr()));
					}
					transactionEstimateDetailVo.setFileList(fileListVO);
				}
			}else{
				return null;
			}
		}else{
			return null;
		}

		return transactionEstimateDetailVo;
	}
	@Transactional
	public int postTransactionPay(Integer requestFormNo, MileageAddDto mileageAddDto) {
		mileageAddDto.setRequestFormNo(requestFormNo);
		mileageAddDto.setRegisterNo(SecurityUtil.getMemberNo());
		mileageAddDto.setMileageSe("B");
		mileageAddDto.setAddPaySe("N");

		int result = frontTransactionMapper.insertTransactionPay(mileageAddDto);

		if(result > 0){
			if(mileageAddDto.getRequestFormPayDc() != null){
				frontTransactionMapper.updateRequestFormDc(mileageAddDto);
			}
			// 2024-09-23 cjj 결제 완료 후 디자이너한테 보이도록 수정
			frontTransactionMapper.updateTransactionHistory(mileageAddDto);
			// 2024-09-23 cjj 지정요청 일때 지정요청서 채팅을 전송한다.

			/*
			2024-10-02 jjchoi
			@최정주 @박성현
			최종적으로 정리된 내용 공유드립니다.
			아래로 작업 부탁드려요!
			1.채팅 내 거래내역 빼기
			2.채팅은 알림톡에서 아예 빼기
			*/
			/*ChatRoomAddDto chatRoomAddDto = new ChatRoomAddDto();
			chatRoomAddDto = frontTransactionMapper.selectTransactionChat(requestFormNo);

			if("B".equals(chatRoomAddDto.getRequestFormSe())){	// 지정 요청시에만 채팅을 보낸다.
				//채팅처리 예제
				chatRoomAddDto.setMemberSe("C");

				int chatRoomNo = chatService.postChatRoom(chatRoomAddDto);

				ChatAddDto chatAddDto = new ChatAddDto();
				chatAddDto.setMsgType("4");
				chatAddDto.setFromNo(SecurityUtil.getMemberNo());
				chatAddDto.setToNo(chatRoomAddDto.getTargetNo());
				Map<String,Object> msg = new HashMap<>();
				msg.put("requestFormNo", requestFormNo);
				chatAddDto.setMsg(new Gson().toJson(msg));
				int chatNo = chatService.postChat(chatRoomNo, chatAddDto);

				// 알림톡을 보낸다.
				AlarmTalkDto alarmTalkDto = new AlarmTalkDto();
				alarmTalkDto = commonService.selectRequestInfo(requestFormNo);
				alarmTalkDto.setTemplateCode(AlarmTalkEnum.ARCHITECT_PROJECT_COMPLETION.getCode());
				alarmTalkDto.setReceiverNum(alarmTalkDto.getDesignerHp());
				String content = AlarmTalkEnum.ARCHITECT_PROJECT_COMPLETION.getMessageTemplate();
				String message = content.replace("#{의뢰인}", alarmTalkDto.getRequestNickName())
						.replace("#{project_name}", alarmTalkDto.getRequestFormSj());
				alarmTalkDto.setContent(message);
				result = commonService.sendKaKaoSend(alarmTalkDto);
			}*/
			AlarmTalkDto alarmTalkDto = new AlarmTalkDto();
			alarmTalkDto = commonService.selectRequestInfo(requestFormNo);

			// 2025-03-05 cjj 로그인한 사람이 국내/해외 의뢰인인지 확인.
			// 회원유형 (A:한국인, B:외국인)
			String memberTp = commonService.selectMemberTp(SecurityUtil.getMemberNo());
			String sj = "";
			if("A".equals(memberTp)){
				sj = "의뢰서 수령";
			}else{
				sj = "Receive referral";
			}

			AlarmAddDto alarmAddDto = new AlarmAddDto();
			alarmAddDto.setAlarmSj(sj);

			String message = "";
			if("A".equals(alarmTalkDto.getRequestFormSe())){	// 공개요청
				// 알림 메세지 발송
				message = ConstUtil.DESIGNER_OPEN_MSG1.replace("{의뢰인 닉네임}", alarmTalkDto.getRequestNickName());
				alarmAddDto.setAlarmCn(message);
				alarmAddDto.setAlarmSe("D");
				alarmAddDto.setAlarmUrl("/payment");
				alarmAddDto.setMemberNo(Integer.parseInt(alarmTalkDto.getDesignerNo()));
				
				int isAlarm = commonService.selectAlarm(Integer.parseInt(alarmTalkDto.getDesignerNo()), 10);
                if (isAlarm > 0) {
                	
                	commonService.postAlarm(alarmAddDto);

            		PushDto push = new PushDto();
                    push.setBody(message);
                    commonService.postFCMPush(Integer.parseInt(alarmTalkDto.getDesignerNo()), push, "/payment");
                	
                }
			}else if("B".equals(alarmTalkDto.getRequestFormSe())){	// 지정요청
				// int isAlarm = commonService.selectAlarm(Integer.parseInt(alarmTalkDto.getRegisterNo()), 4);
                // if (isAlarm > 0) {
				// 	String cn = "";
				// 	if("A".equals(memberTp)){
				// 		cn = ConstUtil.REQUEST_TARGET_MSG1;
				// 	}else{
				// 		cn = ConstUtil.REQUEST_TARGET_ENG_MSG1;
				// 	}
				// 	// 알림 메세지 발송
				// 	message = cn.replace("{치자이너 닉네임}", alarmTalkDto.getDesignerNickName());
				// 	alarmAddDto.setAlarmCn(message);
				// 	alarmAddDto.setAlarmSe("D");
				// 	alarmAddDto.setAlarmUrl("/payment");
				// 	alarmAddDto.setMemberNo(Integer.parseInt(alarmTalkDto.getRegisterNo()));
				// 	commonService.postAlarm(alarmAddDto);

				// 	// FCM
				// 	PushDto push = new PushDto();
				// 	push.setBody(message);
				// 	commonService.postFCMPush(Integer.parseInt(alarmTalkDto.getRegisterNo()), push, "/payment");
				// }

				int isAlarm1 = commonService.selectAlarm(Integer.parseInt(alarmTalkDto.getDesignerNo()), 20);
				if (isAlarm1 > 0) {
                	// 알림톡을 보낸다.
                	alarmTalkDto.setTemplateCode(AlarmTalkEnum.ARCHITECT_PROJECT_COMPLETION.getCode());
                	alarmTalkDto.setReceiverNum(alarmTalkDto.getDesignerHp());
                	String content = AlarmTalkEnum.ARCHITECT_PROJECT_COMPLETION.getMessageTemplate();
                	String message1 = content.replace("#{의뢰인}", alarmTalkDto.getRequestNickName())
                			.replace("#{project_name}", alarmTalkDto.getRequestFormSj());
                	alarmTalkDto.setContent(message1);
                	commonService.sendKaKaoSend(alarmTalkDto);
                }

				int isAlarm2 = commonService.selectAlarm(Integer.parseInt(alarmTalkDto.getDesignerNo()), 10);
				if (isAlarm2 > 0) {
					// 알림 메세지 발송
					String cn = "";
					cn = ConstUtil.DESIGNER_TARGET_MSG7;
					String message1 = cn.replace("#{의뢰인}", alarmTalkDto.getRequestNickName())
							.replace("#{project_name}", alarmTalkDto.getRequestFormSj());
					alarmAddDto.setAlarmSj("의뢰서 수령");
					alarmAddDto.setAlarmCn(message1);
					alarmAddDto.setAlarmSe("D");
					alarmAddDto.setAlarmUrl("/payment");
					alarmAddDto.setMemberNo(Integer.parseInt(alarmTalkDto.getDesignerNo()));
					commonService.postAlarm(alarmAddDto);

					// FCM
					PushDto push = new PushDto();
					push.setBody(message1);
					commonService.postFCMPush(Integer.parseInt(alarmTalkDto.getDesignerNo()), push, "/payment");
				}
			}
			
			int isAlarm = commonService.selectAlarm(SecurityUtil.getMemberNo(), 5);
			if (isAlarm > 0) {
				
				String cn = commonService.selectMileageAmount(requestFormNo);
				String content = "";
				if("A".equals(memberTp)){
					sj = "결제완료";
					content = ConstUtil.REQUEST_OPEN_MSG15;
				}else{
					sj = "Payment complete";
					content = ConstUtil.REQUEST_OPEN_ENG_MSG15;
				}

				String message1 = content.replace("#{마일리지}", cn);

            	String msg = sj + cn + "/" + alarmTalkDto.getDesignerNickName();
            			
            	// 알림 메세지 발송
            	alarmAddDto.setAlarmSj(sj);
            	//alarmAddDto.setAlarmCn(cn + "/" + alarmTalkDto.getDesignerNickName());
            	alarmAddDto.setAlarmCn(message1);
            	alarmAddDto.setAlarmSe("F");
            	alarmAddDto.setAlarmUrl("/payment");
            	alarmAddDto.setMemberNo(Integer.parseInt(alarmTalkDto.getRegisterNo()));
            	commonService.postAlarm(alarmAddDto);
            	
            	// FCM
            	PushDto push = new PushDto();
                push.setBody(message1);
                commonService.postFCMPush(Integer.parseInt(alarmTalkDto.getRegisterNo()), push, "/payment");
            }
			
		}

		return result;
	}

	public Map<String, Object> getTransactionContract(Integer requestFormNo) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap = frontTransactionMapper.selectTransactionContract(requestFormNo, SecurityUtil.getMemberNo());
		resultMap.put("requestDocDesc", frontTransactionMapper.selectPaymentDocDesc(requestFormNo));
		return resultMap;
	}

	public int putTransactionContract(Integer requestFormNo, RequestFormContractDto requestFormContractDto) throws Exception {
		requestFormContractDto.setRequestFormNo(requestFormNo);
		requestFormContractDto.setRegisterNo(SecurityUtil.getMemberNo());

		int result = 0;

		AlarmTalkDto alarmTalkDto = new AlarmTalkDto();
		alarmTalkDto = commonService.selectRequestInfo(requestFormNo);

		RequestFormVo requestFormVo = frontTransactionMapper.selectRequestDealStatus(requestFormNo);
		if(!"B".equals(requestFormVo.getRequestDealStatus()) || !"C".equals(requestFormVo.getRequestStatus())) {
			return result;
		}

		// 수락 시
		if("A".equals(requestFormContractDto.getRequestContactSe())) {
			result += frontTransactionMapper.updateRequestDealStatus(requestFormNo, "C", SecurityUtil.getMemberNo());
			
			// Get recipient's member type (A: Korean, B: Foreign)
			String recipientMemberTp = commonService.selectMemberTp(Integer.parseInt(alarmTalkDto.getRegisterNo()));
			
			// Only send KakaoTalk notification if the recipient is domestic (Korean)
			if("A".equals(recipientMemberTp)) {
				// Send KakaoTalk notification for domestic clients
				alarmTalkDto.setTemplateCode(AlarmTalkEnum.PROJECT_COMPLETION_DOCUMENT_SUBMISSION.getCode());
				alarmTalkDto.setReceiverNum(alarmTalkDto.getRequestHp());
				String content = AlarmTalkEnum.PROJECT_COMPLETION_DOCUMENT_SUBMISSION.getMessageTemplate();
				String message = content.replace("#{project_name}", alarmTalkDto.getRequestFormSj())
						.replace("#{치자이너}", commonService.getMemberNickName(String.valueOf(SecurityUtil.getMemberNo())));
				
				alarmTalkDto.setContent(message);
				commonService.sendKaKaoSend(alarmTalkDto);
			} else {
				MailDto mailDto = new MailDto();
				String emailTemplate = EmailUtil.readReceiveHTMLTemplate(alarmTalkDto);
				
				mailDto.setMailTo(alarmTalkDto.getRequestEmail());
				mailDto.setMailSubject("[Dentner]Receive referral");
				mailDto.setMailContent(emailTemplate);
				mailService.mailSend(mailDto);
			}
			
			// Push notification logic remains the same for both domestic and international
			String url = ConstUtil.DESIGNER_ALARM1_URL.replace("{REQUEST_FORM_NO}", requestFormNo.toString());
			
			// Get sender's member type (A: Korean, B: Foreign)
			String memberTp = commonService.selectMemberTp(SecurityUtil.getMemberNo());
			String msg = "";
			if("A".equals(memberTp)) {
				msg = "요청 수락";
			} else {
				msg = "Req Accept";
			}
			
			// Send app notification
			AlarmAddDto alarmAddDto = new AlarmAddDto();
			alarmAddDto.setAlarmSj(msg);
			String message1 = ConstUtil.DESIGNER_TARGET_MSG1;
			alarmAddDto.setAlarmCn(message1);
			alarmAddDto.setAlarmSe("D");
			alarmAddDto.setAlarmUrl(url);
			alarmAddDto.setMemberNo(Integer.parseInt(alarmTalkDto.getDesignerNo()));
			
			int isAlarm = commonService.selectAlarm(Integer.parseInt(alarmTalkDto.getDesignerNo()), 10);
			if (isAlarm > 0) {
				commonService.postAlarm(alarmAddDto);
				
				PushDto push = new PushDto();
				push.setBody(message1);
				commonService.postFCMPush(Integer.parseInt(alarmTalkDto.getDesignerNo()), push, url);
			}
		}else{ // 요청 거절
			result = frontTransactionMapper.updateRequestStatus(requestFormNo, "E", SecurityUtil.getMemberNo());
			if(result > 0){
				result += frontTransactionMapper.insertTransactionRefuse(requestFormContractDto);
				MileageAddDto mileageAddDto = new MileageAddDto();
				mileageAddDto.setRequestFormNo(requestFormNo);
				mileageAddDto.setRegisterNo(SecurityUtil.getMemberNo());
				mileageAddDto.setMileageSe("D");
				mileageAddDto.setAddPaySe("N");

				result += frontTransactionMapper.insertTransactionRefundPay(mileageAddDto);

				// 2025-03-05 cjj 로그인한 사람이 국내/해외 의뢰인인지 확인.
				// 회원유형 (A:한국인, B:외국인)
				String memberTp = commonService.selectMemberTp(SecurityUtil.getMemberNo());
				String msg = "";
				String cn = "";
				if("A".equals(memberTp)){
					msg = "요청 거절";
					cn = ConstUtil.REQUEST_OPEN_MSG20;
				}else{
					msg = "Req Reject";
					cn = ConstUtil.REQUEST_OPEN_ENG_MSG20;
				}

				// 알림 메세지 발송
				AlarmAddDto alarmAddDto = new AlarmAddDto();
				alarmAddDto.setAlarmSj(msg);
				String message = cn.replace("#{요청서}", alarmTalkDto.getRequestFormSj());
				alarmAddDto.setAlarmCn(message);
				alarmAddDto.setAlarmSe("D");
				alarmAddDto.setAlarmUrl(requestFormNo.toString());
				alarmAddDto.setMemberNo(Integer.parseInt(alarmTalkDto.getRegisterNo()));
				
				int isAlarm = commonService.selectAlarm(Integer.parseInt(alarmTalkDto.getRegisterNo()), 4);
	            if (isAlarm > 0) {
	            	commonService.postAlarm(alarmAddDto);
	            	
	            	PushDto push = new PushDto();
	                push.setBody(message);
	                commonService.postFCMPush(Integer.parseInt(alarmTalkDto.getRegisterNo()), push, "/payment");
	            }
			}
		}
		return result;
	}

	public int putTransactionViewer3d(Integer requestFormNo, String viewerSe, String requestFormSe) {
		int result = 0;
		if("A".equals(viewerSe)){ // 건너 뛰기 시 CAD파일 업로드 상태로 변경
			result = frontTransactionMapper.updateRequestDealStatus(requestFormNo, "E", SecurityUtil.getMemberNo());

			AlarmTalkDto alarmTalkDto = new AlarmTalkDto();
			alarmTalkDto = commonService.selectRequestInfo(requestFormNo);

			String url = ConstUtil.DESIGNER_ALARM2_URL.replace("{REQUEST_FORM_NO}", requestFormNo.toString());

			// 2025-03-05 cjj 로그인한 사람이 국내/해외 의뢰인인지 확인.
			// 회원유형 (A:한국인, B:외국인)
			String memberTp = commonService.selectMemberTp(SecurityUtil.getMemberNo());
			String sj = "";
			if("A".equals(memberTp)){
				sj = "CAD파일 업로드";
			}else{
				sj = "CAD file uploaded";
			}

			// 알림 메세지 발송
			AlarmAddDto alarmAddDto = new AlarmAddDto();
			alarmAddDto.setAlarmSj(sj);
			String message = ConstUtil.DESIGNER_OPEN_MSG2;
			alarmAddDto.setAlarmCn(message);
			alarmAddDto.setAlarmSe("D");
			alarmAddDto.setAlarmUrl(url);
			alarmAddDto.setMemberNo(Integer.parseInt(alarmTalkDto.getDesignerNo()));
			
			int isAlarm = commonService.selectAlarm(Integer.parseInt(alarmTalkDto.getDesignerNo()), 10);
            if (isAlarm > 0) {
            	commonService.postAlarm(alarmAddDto);
            	
            	PushDto push = new PushDto();
                push.setBody(message);
                commonService.postFCMPush(Integer.parseInt(alarmTalkDto.getDesignerNo()), push, url);
            }
			
			
		}else{ // 3d뷰어 소통하기 선택 시 3D뷰어소통 상태로 변경
			result = frontTransactionMapper.updateRequestDealStatus(requestFormNo, "D", SecurityUtil.getMemberNo());
			if("A".equals(requestFormSe)){ // 공개요청
				result += frontTransactionMapper.updateRequest3dNext(requestFormNo, SecurityUtil.getMemberNo());
			}else{
				result += frontTransactionMapper.updateRequest3dNextTarget(requestFormNo, SecurityUtil.getMemberNo());
			}

		}
		return result;
	}

	public int putTransactionPay(Integer requestFormPayNo, TransactionCadAddDto transactionCadAddDto, List<MultipartFile> files) {
		transactionCadAddDto.setRequestFormPayNo(requestFormPayNo);
		transactionCadAddDto.setRegisterNo(SecurityUtil.getMemberNo());

		int result = 0;
		result = frontTransactionMapper.updateTransactionAddPay(transactionCadAddDto);

		if(transactionCadAddDto.getFileDel() != null && !"".equals(transactionCadAddDto.getFileDel())){
			fileMapper.deleteFileArr(transactionCadAddDto.getFileDel());
		}

		if(files != null){
			for (int i = 0; i < files.size(); i++) {
				S3FileVO fileVO = s3Upload.upload(files.get(i));
				fileVO.setFileFromNo(transactionCadAddDto.getRequestFormPayNo());
				fileVO.setFileSe("I");
				fileVO.setRegisterNo(SecurityUtil.getMemberNo());
				fileMapper.insertFile(fileVO);
			}
		}

		return result;
	}

	public int putTransactionCad(Integer requestFormNo, TransactionCadAddDto transactionCadAddDto, List<MultipartFile> cadFiles) {
		transactionCadAddDto.setRequestFormNo(requestFormNo);
		transactionCadAddDto.setRegisterNo(SecurityUtil.getMemberNo());

		int result = 0;
		if(transactionCadAddDto.getFileDel() != null && !"".equals(transactionCadAddDto.getFileDel())){
			fileMapper.deleteFileArr(transactionCadAddDto.getFileDel());
		}

		// cad 파일 업로드
		if(!"".equals(transactionCadAddDto.getDocList())){
			ObjectMapper objectMapper = new ObjectMapper();
			List<TransactionCadFileDto> docList;

			try {
				String jsonString = transactionCadAddDto.getDocList();
				docList = objectMapper.readValue(jsonString, new TypeReference<List<TransactionCadFileDto>>(){});

				for (TransactionCadFileDto doc : docList) {
					String fileName = doc.getFileName();
					MultipartFile file = findFileByName(cadFiles, fileName);
					if (file != null) {
						S3FileVO fileVO = s3Upload.uploadToOrgName(file, doc.getFileOrgName());
						fileVO.setFileFromNo(doc.getRequestDocGroupNo());
						fileVO.setFileSe("J");
						fileVO.setRegisterNo(SecurityUtil.getMemberNo());
						result =+ fileMapper.insertFile(fileVO);
					}
				}
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public int putTransactionDocReceive(Integer requestFormNo, String requestFormSe) throws Exception{
		int result = 0;
		int ct = 0;

		if("A".equals(requestFormSe)){
			ct = frontTransactionMapper.selectTransactionEstimateReceive(requestFormNo, SecurityUtil.getMemberNo());
			if(ct > 0){
				return 1;
			}
			result = frontTransactionMapper.updateTransactionDocReceive(requestFormNo, SecurityUtil.getMemberNo());
		}else{
			ct = frontTransactionMapper.selectTransactionReceive(requestFormNo);
			if(ct > 0){
				return 1;
			}
			result = frontTransactionMapper.updateDocReceive(requestFormNo, SecurityUtil.getMemberNo());
		}

		AlarmTalkDto alarmTalkDto = new AlarmTalkDto();
		alarmTalkDto = commonService.selectRequestInfo(requestFormNo);

		// 2025-03-05 cjj 로그인한 사람이 국내/해외 의뢰인인지 확인.
		// 회원유형 (A:한국인, B:외국인)
		String memberTp = commonService.selectMemberTp(Integer.parseInt(alarmTalkDto.getRegisterNo()));

		// 2025.03.26 cjj 카카오 댓글 15
		int cnt = commonService.selectAlarm(Integer.parseInt(alarmTalkDto.getRegisterNo()), 17);
		if (cnt > 0) { // 알림이 켜져 있음.
			if("A".equals(memberTp)){	// 국내 의뢰인인 경우 카카오톡 전송
				alarmTalkDto.setTemplateCode(AlarmTalkEnum.PROJECT_COMPLETION_DOCUMENT_SUBMISSION.getCode());
				alarmTalkDto.setReceiverNum(alarmTalkDto.getRequestHp());
				String content = AlarmTalkEnum.PROJECT_COMPLETION_DOCUMENT_SUBMISSION.getMessageTemplate();
				String message = content.replace("#{치자이너}", alarmTalkDto.getDesignerNickName())
						.replace("#{project_name}", alarmTalkDto.getRequestFormSj());

				alarmTalkDto.setContent(message);
				result = commonService.sendKaKaoSend(alarmTalkDto);
			}else{	// 국외 의뢰인인 경우 이메일 전송
				// 이메일 발송
				MailDto mailDto = new MailDto();
				String emailTemplate = EmailUtil.readReceiveHTMLTemplate(alarmTalkDto);

				mailDto.setMailTo(alarmTalkDto.getRequestEmail());
				mailDto.setMailSubject("[Dentner]Receive referral");
				mailDto.setMailContent(emailTemplate);
				mailService.mailSend(mailDto);
			}
		}

		int cnt1 = commonService.selectAlarm(Integer.parseInt(alarmTalkDto.getRegisterNo()), 4);
		if (cnt1 > 0) {
			String sj = "";
			String cn = "";
			if("A".equals(memberTp)){
				sj = "의뢰서 수령";
				cn = ConstUtil.REQUEST_OPEN_MSG2;
			}else{
				sj = "Receive referral";
				cn = ConstUtil.REQUEST_OPEN_ENG_MSG2;
			}

			// 알림 메세지 발송
			AlarmAddDto alarmAddDto = new AlarmAddDto();
			alarmAddDto.setAlarmSj(sj);
			String message = cn.replace("{치자이너 닉네임}", alarmTalkDto.getDesignerNickName());
			alarmAddDto.setAlarmCn(message);
			alarmAddDto.setAlarmSe("D");
			alarmAddDto.setAlarmUrl("/payment");
			alarmAddDto.setMemberNo(Integer.parseInt(alarmTalkDto.getRegisterNo()));
			commonService.postAlarm(alarmAddDto);

			PushDto push = new PushDto();
			push.setBody(message);
			commonService.postFCMPush(Integer.parseInt(alarmTalkDto.getRegisterNo()), push, "/payment");
		}

		return result;
	}

	public RequestAddPayVo getTransactionAddPay(Integer requestFormNo, String memberSe) {
		RequestAddPayVo requestAddPayVo = new RequestAddPayVo();
		requestAddPayVo = frontTransactionMapper.selectTransactionAddPay(requestFormNo);
		//requestAddPayVo.setDocList(frontTransactionMapper.selectTransactionDoc(requestFormNo, SecurityUtil.getMemberNo()));
		if(requestAddPayVo != null){
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap = frontTransactionMapper.selectTransactionFormAddPay(requestFormNo, SecurityUtil.getMemberNo(), SecurityUtil.getMemberSe());
			resultMap.put("requestDocDesc", frontTransactionMapper.selectPaymentDocDesc(requestFormNo));

			requestAddPayVo.setForm(resultMap);
			requestAddPayVo.setFileList(fileMapper.selectFileList(requestAddPayVo.getRequestFormPayNo(), "I"));

		}

		return requestAddPayVo;
	}

	public int postTransactionAddPay(Integer requestFormNo, MileageAddDto mileageAddDto) {
		mileageAddDto.setRequestFormNo(requestFormNo);
		mileageAddDto.setRegisterNo(SecurityUtil.getMemberNo());
		mileageAddDto.setMileageSe("B");
		mileageAddDto.setAddPaySe("Y");

		int result = frontTransactionMapper.insertTransactionPay(mileageAddDto);
		if(result > 0){
			result =+ frontTransactionMapper.updateRequestDealStatus(requestFormNo, "G", SecurityUtil.getMemberNo());
		}
		
		// 추가금 결제 알림
		int isAlarm = commonService.selectAlarm(SecurityUtil.getMemberNo(), 5);
        if (isAlarm > 0) {
			// 2025-03-05 cjj 로그인한 사람이 국내/해외 의뢰인인지 확인.
			// 회원유형 (A:한국인, B:외국인)
			String memberTp = commonService.selectMemberTp(SecurityUtil.getMemberNo());
			String sj = "";
			if("A".equals(memberTp)){
				sj = "결제완료";
			}else{
				sj = "Payment complete";
			}

            PushDto push = new PushDto();
            push.setBody(sj + " " + mileageAddDto.getContents());
            commonService.postFCMPush(SecurityUtil.getMemberNo(), push, "/payment");
            
            AlarmAddDto alarmAddDto = new AlarmAddDto();
            alarmAddDto.setAlarmSj(sj);
            alarmAddDto.setAlarmCn(mileageAddDto.getContents());
            alarmAddDto.setAlarmSe("F");
            alarmAddDto.setAlarmUrl("/payment");
            alarmAddDto.setMemberNo(SecurityUtil.getMemberNo());
            commonService.postAlarm(alarmAddDto);
            
            // 치자이너 입금 알림
            AlarmTalkDto alarmTalkDto = new AlarmTalkDto();
            alarmTalkDto = commonService.selectRequestInfo(requestFormNo);
            
            int isAlarm2 = commonService.selectAlarm(Integer.parseInt(alarmTalkDto.getDesignerNo()), 11);
            if (isAlarm2 > 0) {
            	
            	alarmAddDto.setAlarmSj("마일리지 입금");
            	alarmAddDto.setAlarmCn(mileageAddDto.getToContents());
            	alarmAddDto.setAlarmSe("F");
            	alarmAddDto.setAlarmUrl("");
            	alarmAddDto.setMemberNo(Integer.parseInt(alarmTalkDto.getDesignerNo()));
            	//commonService.postAlarm(alarmAddDto);
            	
            	push.setBody("마일리지 입금 "+mileageAddDto.getToContents());
            	//commonService.postFCMPush(Integer.parseInt(alarmTalkDto.getDesignerNo()), push, "/mileageOffice");
            }
        }
		
        

		return result;
	}

	public int postTransactionRemaking(Integer requestFormNo, List<MultipartFile> files, RequestRemakingAddDto requestRemakingAddDto) {
		requestRemakingAddDto.setRegisterNo(SecurityUtil.getMemberNo());
		requestRemakingAddDto.setRequestFormNo(requestFormNo);

		int result = frontTransactionMapper.insertTransactionRemaking(requestRemakingAddDto);

		if(result > 0 ){
			if(files != null){
				for (int i = 0; i < files.size(); i++) {
					S3FileVO fileVO = s3Upload.upload(files.get(i));
					fileVO.setFileFromNo(requestRemakingAddDto.getRequestFormRemakingNo());
					fileVO.setFileSe("K");
					fileVO.setRegisterNo(SecurityUtil.getMemberNo());
					fileMapper.insertFile(fileVO);
				}
			}
			result =+ frontTransactionMapper.updateRequestDealStatus(requestFormNo, "E", SecurityUtil.getMemberNo());
			result =+ frontTransactionMapper.updateRequestStatus(requestFormNo, "C", SecurityUtil.getMemberNo());

			AlarmTalkDto alarmTalkDto = new AlarmTalkDto();
			alarmTalkDto = commonService.selectRequestInfo(requestFormNo);
			
			int isAlarm = commonService.selectAlarm(Integer.parseInt(alarmTalkDto.getDesignerNo()), 10);
            if (isAlarm > 0) {
            	String url = ConstUtil.DESIGNER_ALARM2_URL.replace("{REQUEST_FORM_NO}", requestFormNo.toString());
            	
            	// 알림톡을 보낸다.
            	alarmTalkDto.setTemplateCode(AlarmTalkEnum.ARCHITECT_PROJECT_ACCEPTANCE.getCode());
            	alarmTalkDto.setReceiverNum(alarmTalkDto.getDesignerHp());
            	String content = AlarmTalkEnum.ARCHITECT_PROJECT_ACCEPTANCE.getMessageTemplate();
            	String message = content.replace("#{의뢰인}", alarmTalkDto.getRequestNickName())
            			.replace("#{project_name}", alarmTalkDto.getRequestFormSj())
            			.replace("#{치자이너}", alarmTalkDto.getDesignerNickName());
            	alarmTalkDto.setContent(message);
            	result = commonService.sendKaKaoSend(alarmTalkDto);

            	// 알림 메세지 발송
            	AlarmAddDto alarmAddDto = new AlarmAddDto();
            	alarmAddDto.setAlarmSj("재제작 요청");
            	String message1 = ConstUtil.DESIGNER_OPEN_MSG3.replace("{의뢰인 닉네임}", alarmTalkDto.getRequestNickName());
            	alarmAddDto.setAlarmCn(message1);
            	alarmAddDto.setAlarmSe("D");
            	alarmAddDto.setAlarmUrl(url);
            	alarmAddDto.setMemberNo(Integer.parseInt(alarmTalkDto.getDesignerNo()));
            	commonService.postAlarm(alarmAddDto);
            	
            	PushDto push = new PushDto();
                push.setBody(message1);
                commonService.postFCMPush(Integer.parseInt(alarmTalkDto.getDesignerNo()), push, url);
            }

		}

		return result;
	}

	public RequestRemakingVo getTransactionRemaking(Integer requestFormNo) {
		RequestRemakingVo requestRemakingVo = new RequestRemakingVo();
		requestRemakingVo = frontTransactionMapper.selectTransactionRemaking(requestFormNo, SecurityUtil.getMemberNo());
		if(requestRemakingVo != null){
			requestRemakingVo.setFileList(fileMapper.selectFileList(requestRemakingVo.getRequestFormRemakingNo(), "K"));
		}

		return requestRemakingVo;
	}

	public List<RequestCadFileVo> getTransactionCadFile(Integer requestFormNo, String memberSe) {
		return frontTransactionMapper.selectTransactionCadFile(requestFormNo, SecurityUtil.getMemberNo(), SecurityUtil.getMemberSe());
	}

	public int postReview(Integer targetNo, Integer requestFormNo, List<MultipartFile> images, ReviewDto reviewDto) {
		reviewDto.setMemberNo(SecurityUtil.getMemberNo());
		reviewDto.setTargetNo(targetNo);
		reviewDto.setRequestFormNo(requestFormNo);

		int result = 0;
		result = frontTransactionMapper.insertReview(reviewDto);

		if(result > 0){
			if(images != null){
				for (int i = 0; i < images.size(); i++) {
					S3FileVO fileVO = s3Upload.upload(images.get(i));
					fileVO.setFileFromNo(reviewDto.getReviewNo());
					fileVO.setFileSe("C");
					fileVO.setRegisterNo(SecurityUtil.getMemberNo());
					fileMapper.insertFile(fileVO);
				}
			}

			frontTransactionMapper.updateRequestStatus(reviewDto.getTargetNo(), "D", SecurityUtil.getMemberNo());
			frontTransactionMapper.updateRequestDealStatus(reviewDto.getTargetNo(), "H", SecurityUtil.getMemberNo());
		}

		return result;
	}

	public Integer getTransactionMileage() {
		return frontTransactionMapper.selectTransactionMileage(SecurityUtil.getMemberNo());
	}

	public int deleteTransactionAddPay(Integer requestFormNo) {
		int result = frontTransactionMapper.deleteTransactionAddPay(requestFormNo, SecurityUtil.getMemberNo());

		if(result  > 0){
			result =+ frontTransactionMapper.updateRequestDealStatus(requestFormNo, "G", SecurityUtil.getMemberNo());
		}
		return result;
	}

	public int deleteTransactionRemaking(Integer requestFormNo) {
		int result = frontTransactionMapper.deleteTransactionRemaking(requestFormNo, SecurityUtil.getMemberNo());

		if(result  > 0){
			result =+ frontTransactionMapper.updateRequestDealStatus(requestFormNo, "G", SecurityUtil.getMemberNo());
		}
		return result;
	}

    public TransactionStatVo getTransactionStat() {
		return frontTransactionMapper.selectTransactionStat(SecurityUtil.getMemberNo());
    }

	public int postKtApproval(Integer requestFormNo) throws Exception{
		int result = 0;

		// 1. 전송용 문서를 만든다.
		RequestApprovalVo requestApprovalVo = frontTransactionMapper.selectRequestApproval(requestFormNo, SecurityUtil.getMemberNo());
		requestApprovalVo.setDetailList(frontTransactionMapper.selectRequestApprovalDetail(requestFormNo, SecurityUtil.getMemberNo()));
		requestApprovalVo.setRequestDocDesc(frontTransactionMapper.selectPaymentDocDesc(requestFormNo));
		String fileUrl = TextUtil.requestFormMakeText(requestApprovalVo);

		// 2. 문서를 kt 공전소에 보낸다.
		ObjectMapper jsonMapper = new JsonMapper();
		Map<String, Object> resultMap = new HashMap<String, Object>();

		String txId = TextUtil.generateTxId(32);
		String key = "78803169-218f-4517-9041-15e2dc421cd2";
		String apiUrl = "https://api.bcedadev.kt.com/api/v1/eDocInsReq?txid="+txId;

		String corp_mgt_id = "OM_00000000000000255"; // 발급 받은 법인관리식별자 BPCHAR(20)
		String requester_id = "OA_00000000000000228"; // 발급 받은 요청자식별자 BPCHAR(20)
		String norEncData = "";
		String encData = "";

		Map<String, Object> reqMap = new HashMap<>();
		Map<String, Object> docInfo = new HashMap<>();
		Map<String, Object> meta = new HashMap<>();
		String securityRating = "4";
		String mainTitle = requestApprovalVo.getDetailList().get(0).getRequestBakNumber()+requestApprovalVo.getDesignerNo();

		meta.put("security_rating", securityRating);
		meta.put("corp_mgt_id", corp_mgt_id);
		meta.put("requester_id", requester_id);
		meta.put("main_title", mainTitle);
		meta.put("keep_expire_year", "10");
		meta.put("package_desc", "의뢰번호_"+mainTitle);
		norEncData += requester_id;
		norEncData += corp_mgt_id;
		norEncData += mainTitle;
		norEncData += securityRating;
		encData = TextUtil.getHmacSignature(key, norEncData);
		meta.put("encData", encData);

		List<Map<String, Object>> fileList = new ArrayList<>();

		// requestFormMakeText 메서드 호출 및 결과 사용
		String filePath = TextUtil.requestFormMakeText(requestApprovalVo);
		File file = new File(filePath);

		Map<String, Object> fileInfo = new HashMap<>();
		String fileId = "P" + System.currentTimeMillis() + "0001";
		fileInfo.put("attach_file_nm", file.getName());
		fileInfo.put("attach_file_id", fileId);
		norEncData = fileId;
		norEncData += file.getName();
		encData = TextUtil.getHmacSignature(key, norEncData);
		fileInfo.put("encData", encData);
		fileList.add(fileInfo);

		docInfo.put("meta", meta);
		docInfo.put("fileList", fileList);
		reqMap.put("doc_ins_info", docInfo);

		ObjectMapper objectMapper = new ObjectMapper();
		String jsonBody = objectMapper.writeValueAsString(docInfo);

		System.out.println("jsonBody = " + jsonBody);

		// MultipartEntityBuilder 사용
		MultipartEntityBuilder mbuilder = MultipartEntityBuilder.create();
		mbuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		mbuilder.addTextBody("doc_ins_info", jsonBody, ContentType.APPLICATION_JSON);

		// 단일 파일 추가
		mbuilder.addBinaryBody(
				fileId,
				file,
				ContentType.DEFAULT_BINARY,
				URLEncoder.encode(file.getName(), "UTF-8"));

		// Apache HttpClient를 사용한 요청 전송
		HttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(apiUrl);
		httpPost.setHeader("Authorization", "Bearer " + key);
		httpPost.setEntity(mbuilder.build());

		HttpResponse response = httpClient.execute(httpPost);
		HttpEntity entity = response.getEntity();

		if (entity != null) {
			String resultValue = EntityUtils.toString(entity);
			resultMap = jsonMapper.readValue(resultValue, Map.class);
			System.out.println("resultMap = " + resultMap.toString());
			String res_code = resultMap.get("res_code").toString();
			String res_msg = resultMap.get("res_msg").toString();

			if("0000".equals(res_code)){
				result = frontTransactionMapper.updateRequestData(requestFormNo, SecurityUtil.getMemberNo());
			}else{
				throw new Exception(res_msg);
			}
		}
		// 임시 파일 삭제
		file.delete();

		return result;
	}

	public int updateTransactionCancel(Integer requestFormNo, RequestFormCancelDto requestFormCancelDto) throws Exception{
		requestFormCancelDto.setRequestFormNo(requestFormNo);
		requestFormCancelDto.setRegisterNo(SecurityUtil.getMemberNo());
		requestFormCancelDto.setMemberSe(SecurityUtil.getMemberSe());

		int result = frontTransactionMapper.deleteTransactionCancel(requestFormCancelDto);
		AlarmTalkDto alarmTalkDto = new AlarmTalkDto();
		if(result > 0){
			result =+ frontTransactionMapper.insertTransactionCancel(requestFormCancelDto);
			alarmTalkDto = commonService.selectRequestInfo(requestFormNo);
		}else{
			result =+ frontTransactionMapper.insertTransactionCancel(requestFormCancelDto);
			result =+ frontTransactionMapper.deleteTransactionEstimate(requestFormCancelDto);

			alarmTalkDto = commonService.selectEstimateInfo(requestFormNo, SecurityUtil.getMemberNo());
		}

		String content = "";
		if("A".equals(SecurityUtil.getMemberSe())){	// 의뢰인이 거래취소 승인 대기중을 요청하였을때
			// F:거래취소 승인 대기중
			if("F".equals(requestFormCancelDto.getRequestStatus())){
				int isAlarm = commonService.selectAlarm(Integer.parseInt(alarmTalkDto.getDesignerNo()), 10);
				if (isAlarm > 0) {
					// 의뢰인이 거래취소 요청을 하였을 때 -> 치자이너에게 발송
					AlarmAddDto alarmAddDto = new AlarmAddDto();
					alarmAddDto.setAlarmSj("거래 취소 요청");
					String message = ConstUtil.DESIGNER_OPEN_MSG9.replace("#{요청서}", alarmTalkDto.getRequestFormSj());
					alarmAddDto.setAlarmCn(message);
					alarmAddDto.setAlarmSe("D");
					alarmAddDto.setAlarmUrl("/payment");
					alarmAddDto.setMemberNo(Integer.parseInt(alarmTalkDto.getDesignerNo()));
					commonService.postAlarm(alarmAddDto);
					
					// FCM
					PushDto push = new PushDto();
					push.setBody(message);
					commonService.postFCMPush(Integer.parseInt(alarmTalkDto.getDesignerNo()), push, "/payment");
				}
			}else{	// 바로 취소
				int cnt = commonService.selectAlarm(Integer.parseInt(alarmTalkDto.getRegisterNo()), 17);
				if (cnt > 0) { // 알림이 켜져 있음.
					if ("A".equals(alarmTalkDto.getMemberTp())) {    // 국내 의뢰인인 경우 카카오톡 전송
						alarmTalkDto.setTemplateCode(AlarmTalkEnum.PROJECT_REVISION_REQUEST.getCode());
						content = AlarmTalkEnum.PROJECT_REVISION_REQUEST.getMessageTemplate();
						alarmTalkDto.setReceiverNum(alarmTalkDto.getRequestHp());

						// 알림톡을 보낸다.
						String message = content.replace("#{프로젝트}", alarmTalkDto.getRequestFormSj());
						alarmTalkDto.setContent(message);
						result = commonService.sendKaKaoSend(alarmTalkDto);
					}else{
						// 이메일 발송
						MailDto mailDto = new MailDto();
						String emailTemplate = EmailUtil.readCancelHTMLTemplate(alarmTalkDto);

						mailDto.setMailTo(alarmTalkDto.getRequestEmail());
						mailDto.setMailSubject("[Dentner]Request Cancelled");
						mailDto.setMailContent(emailTemplate);
						mailService.mailSend(mailDto);
					}
				}
				
				int isAlarm = commonService.selectAlarm(Integer.parseInt(alarmTalkDto.getRegisterNo()), 4);
				
				if (isAlarm > 0) {
					// 2025-03-05 cjj 로그인한 사람이 국내/해외 의뢰인인지 확인.
					// 회원유형 (A:한국인, B:외국인)
					String msg = "";
					String cn = "";
					if ("A".equals(alarmTalkDto.getMemberTp())) {
						msg = "거래 취소";
						cn = ConstUtil.REQUEST_OPEN_MSG19;
					}else{
						msg = "Txn Cancel";
						cn = ConstUtil.REQUEST_OPEN_ENG_MSG19;
					}

					// 알림 메세지 발송 거래취소 -> 의뢰인에게 발송
					AlarmAddDto alarmAddDto = new AlarmAddDto();
					alarmAddDto.setAlarmSj(msg);
					String message1 = cn.replace("#{프로젝트}", alarmTalkDto.getRequestFormSj());
					alarmAddDto.setAlarmCn(message1);
					alarmAddDto.setAlarmSe("D");
					alarmAddDto.setAlarmUrl("/payment");
					alarmAddDto.setMemberNo(Integer.parseInt(alarmTalkDto.getRegisterNo()));
					commonService.postAlarm(alarmAddDto);
					
					// FCM
					PushDto push = new PushDto();
					push.setBody(message1);
					
					commonService.postFCMPush(Integer.parseInt(alarmTalkDto.getRegisterNo()), push, "/payment");
				}
				

				// 거래 취소시에 100% 환불
				// 2024-10-30 CJJ 취소시 마일리지 환불 추가
				MileageAddDto mileageAddDto = new MileageAddDto();
				mileageAddDto.setRequestFormNo(requestFormNo);
				mileageAddDto.setMileageSe("D");
				mileageAddDto.setAddPaySe("N");

				result += frontTransactionMapper.insertTransactionCancelRefundAllPay(mileageAddDto);
			}

		}else{ // 치자이너가 거래 취소 요청을 했을때
			if("F".equals(requestFormCancelDto.getRequestStatus())){
				int isAlarm = commonService.selectAlarm(Integer.parseInt(alarmTalkDto.getRegisterNo()), 4);
				
				if (isAlarm > 0) {

					// 2025-03-05 cjj 로그인한 사람이 국내/해외 의뢰인인지 확인.
					// 회원유형 (A:한국인, B:외국인)
					String memberTp = commonService.selectMemberTp(SecurityUtil.getMemberNo());
					String msg = "";
					String cn = "";
					if("A".equals(memberTp)){
						msg = "거래 취소";
						cn = ConstUtil.REQUEST_OPEN_MSG18;
					}else{
						msg = "Txn Cancel";
						cn = ConstUtil.REQUEST_OPEN_ENG_MSG18;
					}

					// 알림 메세지 발송 치자이너가 거래취소 요청을 하였을 때
					AlarmAddDto alarmAddDto = new AlarmAddDto();
					alarmAddDto.setAlarmSj(msg);
					String message = cn.replace("#{요청서}", alarmTalkDto.getRequestFormSj());
					alarmAddDto.setAlarmCn(message);
					alarmAddDto.setAlarmSe("D");
					alarmAddDto.setAlarmUrl("/payment");
					alarmAddDto.setMemberNo(Integer.parseInt(alarmTalkDto.getRegisterNo()));
					commonService.postAlarm(alarmAddDto);
					
					// FCM
					PushDto push = new PushDto();
					push.setBody(message);
					
					commonService.postFCMPush(Integer.parseInt(alarmTalkDto.getRegisterNo()), push, "/payment");
				}
			}else{
				int cnt = commonService.selectAlarm(Integer.parseInt(alarmTalkDto.getRegisterNo()), 20);
				if (cnt > 0) { // 알림이 켜져 있음.
					alarmTalkDto.setTemplateCode(AlarmTalkEnum.ARCHITECT_PROJECT_CANCELLATION.getCode());
					content = AlarmTalkEnum.ARCHITECT_PROJECT_CANCELLATION.getMessageTemplate();
					alarmTalkDto.setReceiverNum(alarmTalkDto.getDesignerHp());

					// 알림톡을 보낸다.
					String message = content.replace("#{프로젝트}", alarmTalkDto.getRequestFormSj());
					alarmTalkDto.setContent(message);
					result = commonService.sendKaKaoSend(alarmTalkDto);
				}

				int isAlarm = commonService.selectAlarm(SecurityUtil.getMemberNo(), 10);
				if (isAlarm > 0) {

					// 2025-03-05 cjj 로그인한 사람이 국내/해외 의뢰인인지 확인.
					// 회원유형 (A:한국인, B:외국인)
					String memberTp = commonService.selectMemberTp(SecurityUtil.getMemberNo());
					String msg = "";
					String cn = "";
					if("A".equals(memberTp)){
						msg = "거래 취소";
						cn = ConstUtil.REQUEST_OPEN_MSG19;
					}else{
						msg = "Txn Cancel";
						cn = ConstUtil.REQUEST_OPEN_ENG_MSG19;
					}
					
					// 알림 메세지 발송 거래취소 -> 치자이너에게 발송
					AlarmAddDto alarmAddDto = new AlarmAddDto();
					alarmAddDto.setAlarmSj(msg);
					String message1 = cn.replace("#{프로젝트}", alarmTalkDto.getRequestFormSj());
					alarmAddDto.setAlarmCn(message1);
					alarmAddDto.setAlarmSe("D");
					alarmAddDto.setAlarmUrl("/payment");
					alarmAddDto.setMemberNo(SecurityUtil.getMemberNo());
					commonService.postAlarm(alarmAddDto);
					
					// FCM
					PushDto push = new PushDto();
					push.setBody(message1);
					
					commonService.postFCMPush(SecurityUtil.getMemberNo(), push, "/payment");
				}

				// 거래 취소시에 100% 환불
				// 2024-10-30 CJJ 취소시 마일리지 환불 추가
				MileageAddDto mileageAddDto = new MileageAddDto();
				mileageAddDto.setRequestFormNo(requestFormNo);
				mileageAddDto.setMileageSe("D");
				mileageAddDto.setAddPaySe("N");

				result += frontTransactionMapper.insertTransactionCancelRefundAllPay(mileageAddDto);
			}
		}

		return result;
	}

	public TransactionStatusVo getTransactionStatus(Integer requestFormNo) {
		return frontTransactionMapper.selectTransactionStatus(requestFormNo, SecurityUtil.getMemberNo());
	}

	public TransactionDataVo getTransactionData(Integer requestFormNo) {
		return frontTransactionMapper.selectTransactionData(requestFormNo);
	}

	public int updateTransactionCancelConfirm(Integer requestFormNo) {
		int result = 0;
		result = frontTransactionMapper.updateRequestStatus(requestFormNo, "E", SecurityUtil.getMemberNo());

		AlarmTalkDto alarmTalkDto = new AlarmTalkDto();
		alarmTalkDto = commonService.selectRequestInfo(requestFormNo);

		if(result > 0){

			if("A".equals(SecurityUtil.getMemberSe())){	// 의뢰인
				// 거래 취소시에 100% 환불
				// 2024-12-19 치자이너 취소 요청시 마일리지 환불 추가
				MileageAddDto mileageAddDto = new MileageAddDto();
				mileageAddDto.setRequestFormNo(requestFormNo);
				mileageAddDto.setMileageSe("D");
				mileageAddDto.setAddPaySe("N");

				result += frontTransactionMapper.insertTransactionCancelRefundAllPay(mileageAddDto);

				int isAlarm = commonService.selectAlarm(Integer.parseInt(alarmTalkDto.getDesignerNo()), 17);
				if (isAlarm > 0) {
					AlarmAddDto alarmAddDto = new AlarmAddDto();
					alarmAddDto.setAlarmSj("거래 취소");
					String message1 = ConstUtil.DESIGNER_OPEN_MSG10.replace("#{프로젝트}", alarmTalkDto.getRequestFormSj());
					alarmAddDto.setAlarmCn(message1);
					alarmAddDto.setAlarmSe("D");
					alarmAddDto.setAlarmUrl("/payment");
					alarmAddDto.setMemberNo(Integer.parseInt(alarmTalkDto.getDesignerNo()));
					commonService.postAlarm(alarmAddDto);

					// FCM
					PushDto push = new PushDto();
					push.setBody(message1);

					commonService.postFCMPush(Integer.parseInt(alarmTalkDto.getDesignerNo()), push, "/payment");
				}
			} else {
				MileageAddDto mileageAddDto = new MileageAddDto();
				mileageAddDto.setRequestFormNo(requestFormNo);
				mileageAddDto.setRegisterNo(SecurityUtil.getMemberNo());
				mileageAddDto.setMileageSe("D");
				mileageAddDto.setAddPaySe("N");
				
				result += frontTransactionMapper.insertTransactionCancelRefundPay(mileageAddDto);

				int isAlarm = commonService.selectAlarm(Integer.parseInt(alarmTalkDto.getRegisterNo()), 4);
				if (isAlarm > 0) {
					String msg = "";
					String cn = "";
					if ("A".equals(alarmTalkDto.getMemberTp())) {
						msg = "거래 취소";
						cn = ConstUtil.REQUEST_OPEN_MSG19;
					}else{
						msg = "Txn Cancel";
						cn = ConstUtil.REQUEST_OPEN_ENG_MSG19;
					}

					// 알림 메세지 발송 거래취소 -> 의뢰인에게 발송
					AlarmAddDto alarmAddDto = new AlarmAddDto();
					alarmAddDto.setAlarmSj(msg);
					String message1 = cn.replace("#{프로젝트}", alarmTalkDto.getRequestFormSj());
					alarmAddDto.setAlarmCn(message1);
					alarmAddDto.setAlarmSe("D");
					alarmAddDto.setAlarmUrl("/payment");
					alarmAddDto.setMemberNo(Integer.parseInt(alarmTalkDto.getRegisterNo()));
					commonService.postAlarm(alarmAddDto);

					// FCM
					PushDto push = new PushDto();
					push.setBody(message1);

					commonService.postFCMPush(Integer.parseInt(alarmTalkDto.getRegisterNo()), push, "/payment");
				}
			}
			

		}
		return result;
	}

	public int deleteTransactionHistory(Integer requestFormNo) {
		return frontTransactionMapper.deleteTransactionHistory(SecurityUtil.getMemberSe(), requestFormNo);
	}

	public int updateTransactionCancelReject(Integer requestFormNo) {
		int result = 0;
		result = frontTransactionMapper.updateRequestStatus(requestFormNo, "C", SecurityUtil.getMemberNo());

		if(result > 0){
			/*
			2024-10-02 jjchoi
			@최정주 @박성현
			최종적으로 정리된 내용 공유드립니다.
			아래로 작업 부탁드려요!
			1.채팅 내 거래내역 빼기
			2.채팅은 알림톡에서 아예 빼기
			*/
/*			ChatRoomAddDto chatRoomAddDto = new ChatRoomAddDto();
			chatRoomAddDto = frontTransactionMapper.selectTransactionChat(requestFormNo);
			//채팅처리 예제
			chatRoomAddDto.setMemberSe("C");

			ChatRoomDto chatRoomDto = new ChatRoomDto();
			chatRoomDto.setMemberNo(chatRoomAddDto.getRequestNo());
			chatRoomDto.setTargetSe(chatRoomAddDto.getMemberSe());
			chatRoomDto.setTargetNo(chatRoomAddDto.getTargetNo());

			ChatRoomVo chatRoomVo = chatMapper.selectChatRoomRequestForRequest(chatRoomDto);
			//int chatRoomNo = chatService.postChatRoom(chatRoomAddDto);
			int chatRoomNo = 0;
			if(chatRoomVo == null) {
				ChatRoomAddDto addDto = new ChatRoomAddDto();
				addDto.setMemberNo(chatRoomAddDto.getRequestNo());
				addDto.setTargetNo(chatRoomAddDto.getTargetNo());
				addDto.setMemberSe(chatRoomAddDto.getMemberSe());

				int addRoomResult = chatMapper.insertChatRoom(addDto);
				chatRoomNo = addDto.getRoomNo();
			} else {
				chatRoomNo = chatRoomVo.getRoomNo();
			}

			ChatAddDto chatAddDto = new ChatAddDto();
			chatAddDto.setMsgType("14");

			chatAddDto.setFromNo(SecurityUtil.getMemberNo());
			if("C".equals(SecurityUtil.getMemberSe()) || "B".equals(SecurityUtil.getMemberSe())) {
				chatAddDto.setToNo(chatRoomAddDto.getRequestNo());
			} else if("A".equals(SecurityUtil.getMemberSe())) {
				chatAddDto.setToNo(chatRoomAddDto.getTargetNo());
			}
			Map<String,Object> msg = new HashMap<>();
			msg.put("requestFormNo", requestFormNo);
			chatAddDto.setMsg(new Gson().toJson(msg));
			int chatNo = chatService.postChat(chatRoomNo, chatAddDto);*/
		}

		return result;
	}

	public int postTransaction3dInfo(Integer requestFormNo, List<MultipartFile> files, Request3dInfoAdd request3dInfoAdd) {
		request3dInfoAdd.setRegisterNo(SecurityUtil.getMemberNo());
		request3dInfoAdd.setRequestFormNo(requestFormNo);

		int result = frontTransactionMapper.insertTransaction3dInfo(request3dInfoAdd);

		if(result > 0 ){
			if(files != null){
				for (int i = 0; i < files.size(); i++) {
					S3FileVO fileVO = s3Upload.upload(files.get(i));
					fileVO.setFileFromNo(request3dInfoAdd.getThreeInfoNo());
					fileVO.setFileSe("N");
					fileVO.setRegisterNo(SecurityUtil.getMemberNo());
					frontTransactionMapper.insert3dFile(fileVO);
				}
			}
		}

		return result;
	}

	public List<ThreeInfoVO> getTransaction3dInfo(Integer requestFormNo) {
		List<ThreeInfoVO> list = new ArrayList<ThreeInfoVO>();
		list = frontTransactionMapper.selectTransaction3dInfo(requestFormNo);

		if(list != null && list.size() > 0){
			for (int i = 0; i < list.size(); i++) {
				list.get(i).setFileList(frontTransactionMapper.selectFileList(list.get(i).getThreeInfoNo()));
			}
		}
		return list;
	}

	public int deleteTransaction3dInfo(String threeInfoNoArr) {
		return frontTransactionMapper.deleteTransaction3dInfo(threeInfoNoArr);
	}

	public List<ThreeMemoVO> getTransaction3dMemo(Integer threeFileNo) {
		return frontTransactionMapper.selectTransaction3dMemo(threeFileNo);
	}

	public Request3dMemoAdd postTransaction3dMemo(Integer threeFileNo, Request3dMemoAdd request3dMemoAdd) {
		request3dMemoAdd.setRegisterNo(SecurityUtil.getMemberNo());
		request3dMemoAdd.setThreeFileNo(threeFileNo);

		frontTransactionMapper.insertTransaction3dMemo(request3dMemoAdd);

		request3dMemoAdd = frontTransactionMapper.select3dMemo(request3dMemoAdd);
		return request3dMemoAdd;
	}

	public int deleteTransaction3dMemo(String threeMemoNoArr) {
		return frontTransactionMapper.deleteTransaction3dMemo(threeMemoNoArr);
	}

	public int postTransaction3dAlarmTalk(Integer requestFormNo, String type) throws Exception{
		int result = 1;

		// 알림톡을 보낸다.
		AlarmTalkDto alarmTalkDto = new AlarmTalkDto();
		alarmTalkDto = commonService.selectRequestInfo(requestFormNo);

		String content = "";
		String message = "";
		if("A".equals(type)){	// 의뢰인이 누를 때
			int cnt = commonService.selectAlarm(Integer.parseInt(alarmTalkDto.getDesignerNo()), 20);
			if (cnt > 0) { // 알림이 켜져 있음.
				alarmTalkDto.setTemplateCode(AlarmTalkEnum.VIEW_3D_DESIGNER.getCode());
				alarmTalkDto.setReceiverNum(alarmTalkDto.getDesignerHp());

				content = AlarmTalkEnum.VIEW_3D_DESIGNER.getMessageTemplate();
				message = content.replace("#{의뢰자}", alarmTalkDto.getRequestNickName())
						.replace("#{project_name}", alarmTalkDto.getRequestFormSj());
				alarmTalkDto.setContent(message);
				result = commonService.sendKaKaoSend(alarmTalkDto);
			}

			int cnt1 = commonService.selectAlarm(Integer.parseInt(alarmTalkDto.getDesignerNo()), 10);
			if (cnt1 > 0) {
				String sj = "3D 뷰어 댓글 등록";
				String cn = ConstUtil.DESIGNER_TARGET_MSG8;
				// 알림 메세지 발송
				AlarmAddDto alarmAddDto = new AlarmAddDto();
				alarmAddDto.setAlarmSj(sj);
				String message1 = cn.replace("#{의뢰인}", alarmTalkDto.getRequestNickName())
						.replace("#{project_name}", alarmTalkDto.getRequestFormSj());
				alarmAddDto.setAlarmCn(message1);
				alarmAddDto.setAlarmSe("D");
				alarmAddDto.setAlarmUrl("/payment/comms/"+alarmTalkDto.getRequestFormNo()+"/cad");
				alarmAddDto.setMemberNo(Integer.parseInt(alarmTalkDto.getDesignerNo()));
				commonService.postAlarm(alarmAddDto);

				PushDto push = new PushDto();
				push.setBody(message1);
				commonService.postFCMPush(Integer.parseInt(alarmTalkDto.getDesignerNo()), push,"/payment/comms/"+alarmTalkDto.getRequestFormNo()+"/cad");
			}
		}else{	// 치자이너가 누를 때
			int cnt = commonService.selectAlarm(Integer.parseInt(alarmTalkDto.getRegisterNo()), 17);
			if (cnt > 0) { // 알림이 켜져 있음.
				System.out.println("alarmTalkDto.getMemberTp() = " + alarmTalkDto.getMemberTp());
				if ("A".equals(alarmTalkDto.getMemberTp())) {    // 국내 의뢰인인 경우 카카오톡 전송
					alarmTalkDto.setTemplateCode(AlarmTalkEnum.VIEW_3D_REQUEST.getCode());
					alarmTalkDto.setReceiverNum(alarmTalkDto.getRequestHp());

					content = AlarmTalkEnum.VIEW_3D_REQUEST.getMessageTemplate();
					message = content.replace("#{의뢰자}", alarmTalkDto.getRequestNickName())
							.replace("#{project_name}", alarmTalkDto.getRequestFormSj())
							.replace("#{치자이너}", alarmTalkDto.getDesignerNickName());
					alarmTalkDto.setContent(message);
					result = commonService.sendKaKaoSend(alarmTalkDto);
				}else{
					MailDto mailDto = new MailDto();
					String emailTemplate = EmailUtil.read3dViewerHTMLTemplate(alarmTalkDto);

					mailDto.setMailTo(alarmTalkDto.getRequestEmail());
					mailDto.setMailSubject("[Dentner]New 3D Viewer comment");
					mailDto.setMailContent(emailTemplate);
					mailService.mailSend(mailDto);
					result = 1;
				}
			}

			// 앱알림 or push 알림
			int cnt1 = commonService.selectAlarm(Integer.parseInt(alarmTalkDto.getRegisterNo()), 4);
			if (cnt1 > 0) {
				// 회원유형 (A:한국인, B:외국인)
				String memberTp = commonService.selectMemberTp(Integer.parseInt(alarmTalkDto.getRegisterNo()));
				String sj = "";
				String cn = "";
				if("A".equals(memberTp)){
					sj = "3D 뷰어 댓글 등록";
					cn = ConstUtil.REQUEST_OPEN_MSG11;
				}else{
					sj = "New 3D Viewer comment";
					cn = ConstUtil.REQUEST_OPEN_ENG_MSG11;
				}
				// 알림 메세지 발송
				AlarmAddDto alarmAddDto = new AlarmAddDto();
				alarmAddDto.setAlarmSj(sj);
				String message1 = cn.replace("#{의뢰인}", alarmTalkDto.getRequestNickName())
						.replace("#{project_name}", alarmTalkDto.getRequestFormSj())
								.replace("#{치자이너}", alarmTalkDto.getDesignerNickName());
				alarmAddDto.setAlarmCn(message1);
				alarmAddDto.setAlarmSe("D");
				alarmAddDto.setAlarmUrl("/payment/comms/"+alarmTalkDto.getRequestFormNo()+"/cad");
				alarmAddDto.setMemberNo(Integer.parseInt(alarmTalkDto.getRegisterNo()));
				commonService.postAlarm(alarmAddDto);

				PushDto push = new PushDto();
				push.setBody(message1);
				commonService.postFCMPush(Integer.parseInt(alarmTalkDto.getRegisterNo()), push, "/payment/comms/"+alarmTalkDto.getRequestFormNo()+"/cad");
			}
		}

		return result;
	}
}

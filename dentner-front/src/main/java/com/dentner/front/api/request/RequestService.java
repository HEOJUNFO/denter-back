package com.dentner.front.api.request;

import com.dentner.core.cmmn.dto.*;
import com.dentner.core.cmmn.mapper.ChatMapper;
import com.dentner.core.cmmn.mapper.FileMapper;
import com.dentner.core.cmmn.mapper.FrontRequestMapper;
import com.dentner.core.cmmn.mapper.FrontTransactionMapper;
import com.dentner.core.cmmn.service.MailService;
import com.dentner.core.cmmn.service.S3Upload;
import com.dentner.core.cmmn.vo.*;
import com.dentner.core.util.AlarmTalkEnum;
import com.dentner.core.util.ConstUtil;
import com.dentner.core.util.EmailUtil;
import com.dentner.core.util.SecurityUtil;
import com.dentner.front.api.common.CommonService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class RequestService {

	@Autowired
	FrontRequestMapper frontRequestMapper;

	@Autowired
	FileMapper fileMapper;

	@Autowired
	FrontTransactionMapper frontTransactionMapper;

	@Autowired
	ChatMapper chatMapper;

	@Autowired
	private MailService mailService;
	
	@Resource(name= "commonService")
	CommonService commonService;

	private final S3Upload s3Upload;

	public int postRequestOften(OftenDto oftenDto) {
		oftenDto.setMemberNo(SecurityUtil.getMemberNo());
		return frontRequestMapper.insertRequestOften(oftenDto);
	}


	public List<OftenVo> getOftenList() {
		return frontRequestMapper.selectOftenList(SecurityUtil.getMemberNo());
	}

	public int deleteRequestOften(Integer oftenNo) {
		return frontRequestMapper.deleteRequestOften(SecurityUtil.getMemberNo(), oftenNo);
	}

	public int postRequestValue(ValueDto valueDto) {
		valueDto.setMemberNo(SecurityUtil.getMemberNo());
		return frontRequestMapper.insertRequestValue(valueDto);
	}

	public List<ValueVo> getValueList() {
		return frontRequestMapper.selectValueList(SecurityUtil.getMemberNo());
	}

	public int deleteRequestValue(Integer valueNo) {
		return frontRequestMapper.deleteRequestValue(SecurityUtil.getMemberNo(), valueNo);
	}

	public int postRequestSimple(RequestDocDto requestDocDto, List<MultipartFile> files) throws Exception{
		int result = 0;
		requestDocDto.setRegisterNo(SecurityUtil.getMemberNo());
		requestDocDto.setRequestSe("A");

		requestDocDto.setRequestBakNumber(frontRequestMapper.selectRequestBakNumber(SecurityUtil.getMemberNo()));
		result = frontRequestMapper.insertRequestDocGroup(requestDocDto);
		if(result > 0){
			frontRequestMapper.insertRequestDoc(requestDocDto);

			if(!"".equals(requestDocDto.getTypeList())){
				ObjectMapper objectMapper = new ObjectMapper();
				List<RequestTypeDto> typeList;

				try {
					String jsonString = requestDocDto.getTypeList();
					typeList = objectMapper.readValue(jsonString, new TypeReference<List<RequestTypeDto>>(){});
					if(typeList.size() > 0){
						for (int i = 0; i < typeList.size(); i++) {
							// 2025-03-27 cjj  count 구문 추가
							if("351".equals(typeList.get(i).getRequestTypeValue())
								|| "352".equals(typeList.get(i).getRequestTypeValue())
								|| "355".equals(typeList.get(i).getRequestTypeValue())
								|| "356".equals(typeList.get(i).getRequestTypeValue())
								|| "358".equals(typeList.get(i).getRequestTypeValue())){
								typeList.get(i).setTypeAddCount(Integer.parseInt(typeList.get(i).getDirect()));
							}
							typeList.get(i).setRegisterNo(SecurityUtil.getMemberNo());
							typeList.get(i).setRequestDocNo(requestDocDto.getRequestDocNo());
							frontRequestMapper.insertRequestType(typeList.get(i));
						}
					}
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}
			if(files != null){
				for (int i = 0; i < files.size(); i++) {
					S3FileVO fileVO = s3Upload.uploadToEncrypt(files.get(i));
					fileVO.setFileFromNo(requestDocDto.getRequestDocGroupNo());
					fileVO.setFileSe("G");
					fileVO.setRegisterNo(SecurityUtil.getMemberNo());
					fileMapper.insertFile(fileVO);
				}
			}
		}

		return requestDocDto.getRequestDocGroupNo();
	}
	public int postRequestDetail(RequestDocDto requestDocDto, List<MultipartFile> files) throws Exception{
		int result = 0;
		requestDocDto.setRegisterNo(SecurityUtil.getMemberNo());
		requestDocDto.setRequestSe("B");

		requestDocDto.setRequestBakNumber(frontRequestMapper.selectRequestBakNumber(SecurityUtil.getMemberNo()));
		result = frontRequestMapper.insertRequestDocGroup(requestDocDto);
		if(result > 0){
			ObjectMapper objectMapper = new ObjectMapper();
			List<RequestDocListDto> docList;
			RequestDocListDto docListDto;

			String json = requestDocDto.getDocList();
			docList = objectMapper.readValue(json, new TypeReference<List<RequestDocListDto>>(){});

			if(docList != null){
				if(docList.size() > 0){
					for (int j = 0; j < docList.size(); j++) {
						docList.get(j).setRequestDocGroupNo(requestDocDto.getRequestDocGroupNo());
						docList.get(j).setRegisterNo(SecurityUtil.getMemberNo());
						frontRequestMapper.insertRequestDocDetail(docList.get(j));

						String interestYn = docList.get(j).getInterestYn();
						if("Y".equals(interestYn)){
							ValueDto valueDto = new ValueDto();
							valueDto.setValueCn(docList.get(j).getValueSj());
							valueDto.setMemberNo(SecurityUtil.getMemberNo());
							int valueResult = frontRequestMapper.insertRequestValue(valueDto);
							if(valueResult > 0){
								ValueDataDto valueDataDto = new ValueDataDto();
								valueDataDto.setValueNo(valueDto.getValueNo());
								valueDataDto.setCementGapValue(docList.get(j).getCementGapValue());
								valueDataDto.setExtraGapValue(docList.get(j).getExtraGapValue());
								valueDataDto.setOcclusalDistanceValue(docList.get(j).getOcclusalDistanceValue());
								valueDataDto.setApproximalDistanceValue(docList.get(j).getApproximalDistanceValue());
								valueDataDto.setHeightMinimalValue(docList.get(j).getHeightMinimalValue());
								frontRequestMapper.insertRequestValueData(valueDataDto);
							}
						}

						if(docList.get(j).getTypeDetail() != null){
							RequestTypeDetailDto typeDto = docList.get(j).getTypeDetail();
							if(typeDto != null){
								List<RequestTypeDentalDto> dentalList;
								RequestTypeDentalDto dentalDto;
								// 2025-03-27 cjj  count 구문 추가
								if("351".equals(typeDto.getRequestTypeValue())
										|| "352".equals(typeDto.getRequestTypeValue())
										|| "355".equals(typeDto.getRequestTypeValue())
										|| "356".equals(typeDto.getRequestTypeValue())
										|| "358".equals(typeDto.getRequestTypeValue())){
									typeDto.setTypeAddCount(Integer.parseInt(typeDto.getDirect()));
								}

								typeDto.setRegisterNo(SecurityUtil.getMemberNo());
								typeDto.setRequestDocNo(docList.get(j).getRequestDocNo());
								frontRequestMapper.insertRequestTypeDetail(typeDto);

								if(typeDto.getDentalList() != null){
									if(typeDto.getDentalList().size() > 0){
										for (int k = 0; k < typeDto.getDentalList().size(); k++) {
											typeDto.getDentalList().get(k).setRegisterNo(SecurityUtil.getMemberNo());
											typeDto.getDentalList().get(k).setRequestTypeNo(typeDto.getRequestTypeNo());

											frontRequestMapper.insertRequestTypeDental(typeDto.getDentalList().get(k));
										}
									}
								}
							}
						}
					}
				}
				if(files != null){
					for (int i = 0; i < files.size(); i++) {
						S3FileVO fileVO = s3Upload.uploadToEncrypt(files.get(i));
						fileVO.setFileFromNo(requestDocDto.getRequestDocGroupNo());
						fileVO.setFileSe("G");
						fileVO.setRegisterNo(SecurityUtil.getMemberNo());
						fileMapper.insertFile(fileVO);
					}
				}
			}
		}

		return requestDocDto.getRequestDocGroupNo();
	}

	public List<RequestBasketVo> getTempList(String type) {
		return frontRequestMapper.selectTempList(type, SecurityUtil.getMemberNo());
	}

	public RequestBasketListVo getRequestBasketList(String type, RequestBasketDto requestBasketDto) {
		requestBasketDto.setType(type);
		requestBasketDto.setRegisterNo(SecurityUtil.getMemberNo());
		RequestBasketListVo requestBasketListVo = new RequestBasketListVo();
		List<RequestBasketVo> requestList = frontRequestMapper.selectRequestBasketList(requestBasketDto);

		if(requestList.size() > 0){
			for (int i = 0; i < requestList.size(); i++) {
				requestList.get(i).setRequestDocDesc(frontRequestMapper.selectRequestBasketDocDesc(requestList.get(i).getRequestDocGroupNo(), SecurityUtil.getMemberNo()));
			}
		}

		requestBasketListVo.setList(requestList);
		requestBasketListVo.setCnt(frontRequestMapper.selectRequestBasketListCnt(requestBasketDto));

		return requestBasketListVo;
	}

	public int postRequestPublicForm(RequestFormAddDto requestFormAddDto) {
		requestFormAddDto.setRegisterNo(SecurityUtil.getMemberNo());
		requestFormAddDto.setRequestStatus("A");

		int result = frontRequestMapper.insertRequestForm(requestFormAddDto);
		if(result > 0){
			AlarmTalkDto alarmTalkDto = new AlarmTalkDto();
			alarmTalkDto = commonService.selectRequestInfo(requestFormAddDto.getRequestFormNo());
			alarmTalkDto.setTemplateCode(AlarmTalkEnum.PROJECT_CREATION.getCode());
			alarmTalkDto.setReceiverNum("01093357520");
			String content = AlarmTalkEnum.PROJECT_CREATION.getMessageTemplate();
			String message = content.replace("#{의뢰인}", alarmTalkDto.getRequestNickName())
					.replace("#{관리자}", "관리자");
			alarmTalkDto.setContent(message);
			result = commonService.sendKaKaoSend(alarmTalkDto);
			
			
		}
		return requestFormAddDto.getRequestFormNo();
	}

	public int postRequestTargetForm(RequestFormAddDto requestFormAddDto) {
		requestFormAddDto.setRegisterNo(SecurityUtil.getMemberNo());
		requestFormAddDto.setRequestStatus("C");
		requestFormAddDto.setRequestDealStatus("B");
		int result = frontRequestMapper.insertRequestForm(requestFormAddDto);
		return requestFormAddDto.getRequestFormNo();
	}

	public List<RequestBasketVo> getRequestDocList(String type) {
		List<RequestBasketVo> requestList = frontRequestMapper.selectRequestDocList(type, SecurityUtil.getMemberNo());

		if(requestList.size() > 0){
			for (int i = 0; i < requestList.size(); i++) {
				requestList.get(i).setRequestDocDesc(frontRequestMapper.selectRequestBasketDocDesc(requestList.get(i).getRequestDocGroupNo(), SecurityUtil.getMemberNo()));
			}
		}
		return requestList;
	}

	public List<TargetDesignerVo> getRequestDesignerList() {
		return frontRequestMapper.selectRequestDesingerList(SecurityUtil.getMemberNo());
	}

	public RequestFormListVo getRequestFormList(String requestFormSe, RequestFormDto requestFormDto) {
		requestFormDto.setRequestFormSe(requestFormSe);
		requestFormDto.setRegisterNo(SecurityUtil.getMemberNo());
		requestFormDto.setMemberSe(SecurityUtil.getMemberSe());

		if("B".equals(requestFormSe)){
			requestFormDto.setMyFilter("Y");
		}

		RequestFormListVo requestFormListVo = new RequestFormListVo();
		requestFormListVo.setList(frontRequestMapper.selectRequestFormList(requestFormDto));
		requestFormListVo.setCnt(frontRequestMapper.selectRequestFormListCnt(requestFormDto));

		return requestFormListVo;
	}

	public RequestFormProstheticsVo getRequestFormProsthetics(Integer requestFormNo) {
		RequestFormProstheticsVo requestFormProstheticsVo = new RequestFormProstheticsVo();
		List<HashMap<String, Object>> requestList = new ArrayList<HashMap<String, Object>>();
		requestList = frontRequestMapper.selectRequestFormRequestList(requestFormNo);

		if(requestList.size() > 0){
			for (int i = 0; i < requestList.size(); i++) {
				//requestList.get(i).put("requestDocDesc", frontRequestMapper.selectRequestFormEstimateDocDesc(requestFormNo));
			}
		}

		requestFormProstheticsVo.setRequestList(requestList);
		requestFormProstheticsVo.setProstheticsList(frontRequestMapper.selectRequestDetailProstheticsList(requestFormNo));

		return requestFormProstheticsVo;
	}

	public RequestFormDetailVo getRequestFormDetail(Integer requestFormNo) {
		RequestFormDetailVo requestFormDetailVo = new RequestFormDetailVo();
		requestFormDetailVo = frontRequestMapper.selectRequestFormDetail(requestFormNo, SecurityUtil.getMemberNo());
		if(requestFormDetailVo != null){
			boolean myFlag = true;
			if("B".equals(requestFormDetailVo.getRequestFormSe())){
				if("A".equals(SecurityUtil.getMemberSe())){
					if(SecurityUtil.getMemberNo() != requestFormDetailVo.getRegisterNo()){
						myFlag = false;
					}
				}else{
					if(SecurityUtil.getMemberNo() != requestFormDetailVo.getRequestDesignerNo()){
						myFlag = false;
					}
				}
			}
			if(myFlag){
				List<HashMap<String, Object>> prostheticsList = frontRequestMapper.selectRequestDetailProstheticsList(requestFormNo);
				if(prostheticsList != null && prostheticsList.size() > 0){
					int targetAmount = 0;
					for (int i = 0; i < prostheticsList.size(); i++) {
						targetAmount += (Integer.parseInt(prostheticsList.get(i).get("amount").toString()) * Integer.parseInt(prostheticsList.get(i).get("count").toString()));
					}
					requestFormDetailVo.setTargetAmount(targetAmount);
				}
				requestFormDetailVo.setProstheticsList(prostheticsList);
				// 내 의뢰서 일때만 의뢰서 정보 보이도록
				if(requestFormDetailVo.getRegisterNo() == SecurityUtil.getMemberNo()){
					requestFormDetailVo.setRequestList(frontRequestMapper.selectRequestDetailRequestList(requestFormNo, SecurityUtil.getMemberNo()));
				}
				requestFormDetailVo.setReplyList(frontRequestMapper.selectRequestDetailReplyList(requestFormNo, SecurityUtil.getMemberNo()));

				List<HashMap<String, Object>> requestList = new ArrayList<HashMap<String, Object>>();
				requestList = frontRequestMapper.selectRequestFormRequestList(requestFormNo);

				if(requestList.size() > 0){
					for (int i = 0; i < requestList.size(); i++) {
						//requestList.get(i).put("requestDocDesc", frontRequestMapper.selectRequestFormEstimateDocDesc(requestFormNo));
					}
				}
				requestFormDetailVo.setProsthetics(requestList);
			}else{
				return null;
			}
		}else{
			return null;
		}

		return requestFormDetailVo;
	}

	public int postReportRequestForm(Integer requestFormNo, Integer targetNo, ReportDto reportDto) {
		reportDto.setMemberNo(SecurityUtil.getMemberNo());
		reportDto.setTargetNo(targetNo);
		reportDto.setReportTp("B");
		reportDto.setReportTargetNo(requestFormNo);
		return frontRequestMapper.insertReportRequestForm(reportDto);
	}

	public int postRequestFormReply(ReplyAddDto replyAddDto) throws Exception{
		replyAddDto.setRegisterNo(SecurityUtil.getMemberNo());
		int result = frontRequestMapper.insertRequestFormReply(replyAddDto);
		if(result > 0){
			
			AlarmTalkDto alarmTalkDto = new AlarmTalkDto();
			alarmTalkDto = commonService.selectRequestInfo(replyAddDto.getRequestFormNo());

			// 2025-03-05 cjj 로그인한 사람이 국내/해외 의뢰인인지 확인.
			// 회원유형 (A:한국인, B:외국인)
			String memberTp = commonService.selectMemberTp(Integer.parseInt(alarmTalkDto.getRegisterNo()));
			// 댓글 작성시 의뢰인에게 알림톡 전송
			if ("0".equals(replyAddDto.getParentAnswerNo().toString())) {
				if (Integer.parseInt(alarmTalkDto.getRegisterNo()) != SecurityUtil.getMemberNo()) {

					// 2025.03.26 cjj 카카오 댓글 15
					int cnt = commonService.selectAlarm(Integer.parseInt(alarmTalkDto.getRegisterNo()), 15);
					if (cnt > 0) { // 알림이 켜져 있음.
						if("A".equals(memberTp)){	// 국내 의뢰인인 경우 카카오톡 전송
							alarmTalkDto.setTemplateCode(AlarmTalkEnum.PROJECT_PAYMENT.getCode());
							alarmTalkDto.setReceiverNum(alarmTalkDto.getRequestHp());
							String content = AlarmTalkEnum.PROJECT_PAYMENT.getMessageTemplate();
							String message = content.replace("#{의뢰인}", alarmTalkDto.getRequestNickName())
									.replace("#{project_name}", alarmTalkDto.getRequestFormSj());

							alarmTalkDto.setContent(message);
							result = commonService.sendKaKaoSend(alarmTalkDto);
						}else{	// 국외 의뢰인인 경우 이메일 전송
							// 이메일 발송
							MailDto mailDto = new MailDto();
							alarmTalkDto.setMsg(replyAddDto.getAnswerCn());
							String emailTemplate = EmailUtil.readReplyHTMLTemplate(alarmTalkDto);

							mailDto.setMailTo(alarmTalkDto.getRequestEmail());
							mailDto.setMailSubject("[Dentner]New comment");
							mailDto.setMailContent(emailTemplate);
							mailService.mailSend(mailDto);
						}
					}

					// 앱알림 or push 알림
					int cnt1 = commonService.selectAlarm(Integer.parseInt(alarmTalkDto.getRegisterNo()), 2);
					if (cnt1 > 0) {
						String sj = "";
						if("A".equals(memberTp)){
							sj = "새로운 댓글 등록";
						}else{
							sj = "New comment";
						}
						// 알림 메세지 발송
						AlarmAddDto alarmAddDto = new AlarmAddDto();
						alarmAddDto.setAlarmSj(sj);
						String content = ConstUtil.REPLY_MSG1;
						String message1 = content.replace("{작성자 닉네임}", commonService.getMemberNickName(String.valueOf(SecurityUtil.getMemberNo())))
								.replace("{댓글내용}", replyAddDto.getAnswerCn());
						alarmAddDto.setAlarmCn(message1);
						alarmAddDto.setAlarmSe("D");
						alarmAddDto.setAlarmUrl(replyAddDto.getUrl());
						alarmAddDto.setMemberNo(Integer.parseInt(alarmTalkDto.getRegisterNo()));
						commonService.postAlarm(alarmAddDto);

						PushDto push = new PushDto();
						push.setBody(message1);

						commonService.postFCMPush(Integer.parseInt(alarmTalkDto.getRegisterNo()), push, replyAddDto.getUrl());
					}
				}
			}
			
			// 답글 작성시 알림톡 전송 
			if (replyAddDto.getParentAnswerNo() > 0) {
				
				MemberVo memberVo = frontRequestMapper.selectParentAnswerInfo(replyAddDto);
				if(memberVo.getMemberNo() == SecurityUtil.getMemberNo()){
					return result;
				}
				// 치자이너,치기공소인 경우 전송
				if (!"A".equals(memberVo.getMemberSe())) {
					// 카카오톡 알림
					int cnt = commonService.selectAlarm(memberVo.getMemberNo(), 19);
					if (cnt > 0) {
						alarmTalkDto.setTemplateCode(AlarmTalkEnum.ARCHITECT_COMMENT_ON_REQUEST.getCode());
						alarmTalkDto.setReceiverNum(memberVo.getMemberHp());
						String content = AlarmTalkEnum.ARCHITECT_COMMENT_ON_REQUEST.getMessageTemplate();
						String message = content.replace("#{치과기공소or치자이너}", memberVo.getMemberNickName())
								.replace("#{project_name}", alarmTalkDto.getRequestFormSj());

						alarmTalkDto.setContent(message);
						result = commonService.sendKaKaoSend(alarmTalkDto);
					}
					
					int cnt1 = commonService.selectAlarm(memberVo.getMemberNo(), 9);
					if (cnt1 > 0) {
						AlarmAddDto alarmAddDto = new AlarmAddDto();
						alarmAddDto.setAlarmSj("새로운 대댓글 등록");
						String content1 = ConstUtil.REPLY_MSG1;
						String message1 = content1.replace("{작성자 닉네임}", commonService.getMemberNickName(String.valueOf(SecurityUtil.getMemberNo())))
								.replace("{댓글내용}", replyAddDto.getAnswerCn());
						alarmAddDto.setAlarmCn(message1);
						alarmAddDto.setAlarmSe("D");
						alarmAddDto.setAlarmUrl(replyAddDto.getUrl());
						alarmAddDto.setMemberNo(memberVo.getMemberNo());
						commonService.postAlarm(alarmAddDto);
						
						PushDto push = new PushDto();
						push.setBody(message1);
						
						commonService.postFCMPush(memberVo.getMemberNo(), push, replyAddDto.getUrl());
					}
					
				} else {
					// 의뢰인일 경우
					if (Integer.parseInt(alarmTalkDto.getRegisterNo()) == memberVo.getMemberNo()) {
						// 카카오톡 알림 or 이메일 알림
						int cnt = commonService.selectAlarm(memberVo.getMemberNo(), 15);
						if (cnt > 0) {
							if("A".equals(memberTp)){	// 국내 의뢰인인 경우 카카오톡 전송
								alarmTalkDto.setTemplateCode(AlarmTalkEnum.PAYMENT_RECEIVED.getCode());
								alarmTalkDto.setReceiverNum(memberVo.getMemberHp());
								String content = AlarmTalkEnum.PAYMENT_RECEIVED.getMessageTemplate();
								String message = content.replace("#{의뢰자}", memberVo.getMemberNickName())
										.replace("#{project_name}", alarmTalkDto.getRequestFormSj())
										.replace("#{comment_user_name}", commonService.getMemberNickName(String.valueOf(SecurityUtil.getMemberNo())));
								alarmTalkDto.setContent(message);
								result = commonService.sendKaKaoSend(alarmTalkDto);
							}else{	// 국외 의뢰인인 경우 이메일 전송
								// 이메일 발송
								MailDto mailDto = new MailDto();
								alarmTalkDto.setMsg(replyAddDto.getAnswerCn());
								String emailTemplate = EmailUtil.readReplyReHTMLTemplate(alarmTalkDto);

								mailDto.setMailTo(alarmTalkDto.getRequestEmail());
								mailDto.setMailSubject("[Dentner]New sub-comment");
								mailDto.setMailContent(emailTemplate);

								mailService.mailSend(mailDto);
							}
						}

						// 앱 알림
						int cnt1 = commonService.selectAlarm(memberVo.getMemberNo(), 2);
						if (cnt1 > 0) {

							// 2025-03-05 cjj 로그인한 사람이 국내/해외 의뢰인인지 확인.
							// 회원유형 (A:한국인, B:외국인)
							String sj = "";
							if("A".equals(memberTp)){
								sj = "새로운 대댓글 등록";
							}else{
								sj = "New sub-comment";
							}

							AlarmAddDto alarmAddDto = new AlarmAddDto();
							alarmAddDto.setAlarmSj(sj);
							String content1 = ConstUtil.REPLY_MSG1;
							String message1 = content1.replace("{작성자 닉네임}", commonService.getMemberNickName(String.valueOf(SecurityUtil.getMemberNo())))
									.replace("{댓글내용}", replyAddDto.getAnswerCn());
							alarmAddDto.setAlarmCn(message1);
							alarmAddDto.setAlarmSe("D");
							alarmAddDto.setAlarmUrl(replyAddDto.getUrl());
							alarmAddDto.setMemberNo(Integer.parseInt(alarmTalkDto.getRegisterNo()));
							commonService.postAlarm(alarmAddDto);
							
							PushDto push = new PushDto();
							push.setBody(message1);
							
							commonService.postFCMPush(memberVo.getMemberNo(), push, replyAddDto.getUrl());
						}
					}
				}
			}
		}

		return result;
	}

	public int putRequestFormReply(Integer requestFormAnswerNo, ReplyAddDto replyAddDto) {
		replyAddDto.setRequestFormAnswerNo(requestFormAnswerNo);
		replyAddDto.setRegisterNo(SecurityUtil.getMemberNo());

		return frontRequestMapper.updateRequestFormReply(replyAddDto);
	}

	public int postReportReplyForm(Integer requestFormAnswerNo, Integer targetNo, ReportDto reportDto) {
		reportDto.setMemberNo(SecurityUtil.getMemberNo());
		reportDto.setTargetNo(targetNo);
		reportDto.setReportTp("C");
		reportDto.setReportTargetNo(requestFormAnswerNo);
		return frontRequestMapper.insertReportRequestForm(reportDto);
	}

	public int deleteRequestFormReply(Integer requestFormAnswerNo) {
		return frontRequestMapper.deleteRequestFormReply(requestFormAnswerNo);
	}

	public int deleteRequestForm(String requestFormNoArr) {
		String[] requestArr = requestFormNoArr.split(",");
		if(requestArr.length > 0){
			for (int i = 0; i < requestArr.length; i++) {

			}
		}
		return frontRequestMapper.deleteRequestForm(requestFormNoArr);
	}

	public int getRequestFormTargetAmount(Integer targetNo, String requestDocGroupsNo) {
		List<String> docList = null;
		if (requestDocGroupsNo != null && !requestDocGroupsNo.isEmpty()) {
			docList = Arrays.asList(requestDocGroupsNo.split(","));
		}
		return frontRequestMapper.selectRequestFormTargetAmount(targetNo, docList);
	}

	public List<ReplyVo> getRequestFormReplyList(Integer requestFormNo) {
		return frontRequestMapper.selectRequestDetailReplyList(requestFormNo, SecurityUtil.getMemberNo());
	}

	public int postRequestEstimate(Integer requestFormNo, EstimateAddDto estimateAddDto) throws Exception{
		estimateAddDto.setRegisterNo(SecurityUtil.getMemberNo());
		estimateAddDto.setRequestFormNo(requestFormNo);

		int cnt = frontRequestMapper.selectRequestEstimateCnt(estimateAddDto);
		if(cnt > 0){
			throw new Exception("이미 작성한 견적서가 있습니다.");
		}

		int result = frontRequestMapper.insertRequestEstimate(estimateAddDto);

		if(result > 0){
			if(estimateAddDto.getTypeList() != null){
				if(estimateAddDto.getTypeList().size() > 0){
					for (int i = 0; i < estimateAddDto.getTypeList().size(); i++) {
						estimateAddDto.getTypeList().get(i).setRequestEstimateNo(estimateAddDto.getRequestEstimateNo());
						estimateAddDto.getTypeList().get(i).setRegisterNo(SecurityUtil.getMemberNo());
						result += frontRequestMapper.insertRequestEstimateType(estimateAddDto.getTypeList().get(i));
					}
				}
			}
			// 요청서 상태 변경
			frontTransactionMapper.updateRequestStatus(requestFormNo, "B", SecurityUtil.getMemberNo());

			// 바로가기 url			
			String url = ConstUtil.REQUEST_ALARM3_URL.replace("{REQUEST_ESTIMATE_NO}", estimateAddDto.getRequestEstimateNo().toString());
			
			// 2024-10-02 jjchoi 알림톡 추가
			// 알림톡을 보낸다.
			AlarmTalkDto alarmTalkDto = new AlarmTalkDto();
			alarmTalkDto = commonService.selectEstimateInfo(requestFormNo, SecurityUtil.getMemberNo());

			int cnt1 = commonService.selectAlarm(Integer.parseInt(alarmTalkDto.getRegisterNo()), 16);
			if (cnt1 > 0) { // 알림이 켜져 있음.
				if ("A".equals(alarmTalkDto.getMemberTp())) {
					alarmTalkDto.setTemplateCode(AlarmTalkEnum.PROJECT_CONTRACT_DOCUMENT_CREATION.getCode());
					alarmTalkDto.setReceiverNum(alarmTalkDto.getRequestHp());
					String content = AlarmTalkEnum.PROJECT_CONTRACT_DOCUMENT_CREATION.getMessageTemplate();
					String message = content.replace("#{치자이너}", alarmTalkDto.getDesignerNickName())
							.replace("#{project_name}", alarmTalkDto.getRequestFormSj());
					alarmTalkDto.setContent(message);
					result = commonService.sendKaKaoSend(alarmTalkDto);
				}else{
					MailDto mailDto = new MailDto();
					alarmTalkDto.setDesignerNo(String.valueOf(SecurityUtil.getMemberNo()));
					String emailTemplate = EmailUtil.readEstimateHTMLTemplate(alarmTalkDto);
					mailDto.setMailTo(alarmTalkDto.getRequestEmail());
					mailDto.setMailSubject("[Dentner]request quote");
					mailDto.setMailContent(emailTemplate);
					mailService.mailSend(mailDto);
				}
			}


			int isAlarm = commonService.selectAlarm(Integer.parseInt(alarmTalkDto.getRegisterNo()), 3);
			if (isAlarm > 0) {
				// 2025-03-05 cjj 로그인한 사람이 국내/해외 의뢰인인지 확인.
				// 회원유형 (A:한국인, B:외국인)
				String memberTp = commonService.selectMemberTp(Integer.parseInt(alarmTalkDto.getRegisterNo()));
				String sj = "";
				String cn = "";
				if("A".equals(memberTp)){
					sj = "견적 도착";
					cn = ConstUtil.REQUEST_OPEN_MSG1;
				}else{
					sj = "Quote received";
					cn = ConstUtil.REQUEST_OPEN_ENG_MSG1;
				}
				
				// 알림 메세지 발송
				AlarmAddDto alarmAddDto = new AlarmAddDto();
				alarmAddDto.setAlarmSj(sj);
				alarmAddDto.setAlarmCn(cn);
				alarmAddDto.setAlarmSe("B");
				alarmAddDto.setAlarmUrl(url);
				alarmAddDto.setMemberNo(Integer.parseInt(alarmTalkDto.getRegisterNo()));
				commonService.postAlarm(alarmAddDto);
				
				// FCM
				PushDto push = new PushDto();
				push.setBody(cn);
				
				commonService.postFCMPush(Integer.parseInt(alarmTalkDto.getRegisterNo()), push, url);
			}
			
			// 채팅방 있는지 확인.
			/*
			2024-10-02 jjchoi
			@최정주 @박성현
			최종적으로 정리된 내용 공유드립니다.
			아래로 작업 부탁드려요!
			1.채팅 내 거래내역 빼기
			2.채팅은 알림톡에서 아예 빼기
			*/
			/*RequestFormDetailVo chatRequdstForm = frontRequestMapper.selectRequestFormDetailForChat(requestFormNo);
			int memberNo = chatRequdstForm.getRegisterNo();// 요청서 작성 의뢰인 번호
			int targetNo = estimateAddDto.getRegisterNo(); // 견적서 전송 치자이너 or 치과기공소 번호
			String memberSe = "C";
			ChatRoomDto chatRoomDto = new ChatRoomDto();
			chatRoomDto.setMemberNo(memberNo);
			chatRoomDto.setTargetSe(memberSe);
			chatRoomDto.setTargetNo(targetNo);
			ChatRoomVo chatRoomVo = chatMapper.selectChatRoomRequestForRequest(chatRoomDto);

			if(chatRoomVo == null) {//방이 없다면 생성
				ChatRoomAddDto addDto = new ChatRoomAddDto();
				addDto.setMemberNo(memberNo);
				addDto.setTargetNo(targetNo);
				addDto.setMemberSe(memberSe);

				int addRoomResult = chatMapper.insertChatRoom(addDto);
				//
				ChatAddDto chatAddDto = new ChatAddDto();
				chatAddDto.setRoomNo(addDto.getRoomNo());
				chatAddDto.setFromNo(targetNo);
				chatAddDto.setToNo(memberNo);
				Map<String,Object> msg = new HashMap<>();
				msg.put("requestEstimateNo", estimateAddDto.getRequestEstimateNo());
				msg.put("requestFormNo", requestFormNo);
				chatAddDto.setMsg(new Gson().toJson(msg));
				chatAddDto.setMsgType("8");

				chatMapper.insertChat(chatAddDto);

			} else { //방이 있다면 메세지 전송
				ChatAddDto chatAddDto = new ChatAddDto();
				chatAddDto.setRoomNo(chatRoomVo.getRoomNo());
				chatAddDto.setFromNo(targetNo);
				chatAddDto.setToNo(memberNo);
				Map<String,Object> msg = new HashMap<>();
				msg.put("requestEstimateNo", estimateAddDto.getRequestEstimateNo());
				msg.put("requestFormNo", requestFormNo);
				chatAddDto.setMsg(new Gson().toJson(msg));
				chatAddDto.setMsgType("8");
				chatMapper.insertChat(chatAddDto);
			}*/
		}
		return result;
	}

	public int deleteRequestDoc(String requestDocGroupArr) {
		int result = frontRequestMapper.deleteRequestDocGroup(requestDocGroupArr);
		if(result > 0){
			result += frontRequestMapper.deleteRequestDoc(requestDocGroupArr);
		}

		return result;
	}

	public RequestEstimateDetailVo getRequestEstimate(Integer requestFormNo) {
		RequestEstimateDetailVo requestEstimateDetailVo = new RequestEstimateDetailVo();
		requestEstimateDetailVo = frontRequestMapper.selectRequestFormEstimateDetail(requestFormNo, SecurityUtil.getMemberNo());
		if(!"B".equals(requestEstimateDetailVo.getRequestFormSe())){
			requestEstimateDetailVo.setProstheticsList(frontRequestMapper.selectRequestEstimateProstheticsList(requestFormNo, SecurityUtil.getMemberNo()));
			requestEstimateDetailVo.setRequestDocDesc(frontRequestMapper.selectRequestFormEstimateDocDesc(requestFormNo));
		}else{
			return null;
		}

		return requestEstimateDetailVo;
	}

	public int getRequestEstimateStatus(Integer requestFormNo) {
		Integer result = frontRequestMapper.selectRequestEstimateStatus(requestFormNo, SecurityUtil.getMemberNo());
		if(result == null){
			result = 0;
		}
		return result;
	}

	public int putRequestForm(RequestFormAddDto requestFormAddDto) {
		requestFormAddDto.setRegisterNo(SecurityUtil.getMemberNo());
		return frontRequestMapper.updateRequestForm(requestFormAddDto);
	}

	public int postRequestTargetFormAgree(Integer requestFormNo) {
		int result = 0;
		result = frontTransactionMapper.updateRequestStatus(requestFormNo, "C", SecurityUtil.getMemberNo());
		if(result > 0){
			result =+ frontTransactionMapper.updateRequestDealStatus(requestFormNo, "B", SecurityUtil.getMemberNo());
		}
		return result;
	}

	public int postRequestTargetFormRefuse(Integer requestFormNo, RequestFormRefuseDto requestFormRefuseDto) {
		requestFormRefuseDto.setRequestFormNo(requestFormNo);
		requestFormRefuseDto.setRegisterNo(SecurityUtil.getMemberNo());

		int result = frontRequestMapper.insertRequestTargetFormRefuse(requestFormRefuseDto);
		if(result > 0){
			result =+ frontTransactionMapper.updateRequestStatus(requestFormNo, "H", SecurityUtil.getMemberNo());
		}

		return result;
	}

	public RequestJsonVo getRequestJson(Integer requestDocGroupNo) {
		RequestJsonVo requestJsonVo = new RequestJsonVo();
		requestJsonVo = frontRequestMapper.selectRequestJson(requestDocGroupNo, SecurityUtil.getMemberNo());
		requestJsonVo.setFileList(frontRequestMapper.selectRequestDocFileList(requestDocGroupNo));
		requestJsonVo.setKeyList(frontRequestMapper.selectRequestDocKeyList(requestDocGroupNo));
		return requestJsonVo;
	}

	public int putRequestSimple(Integer requestDocGroupNo, RequestDocDto requestDocDto, List<MultipartFile> files) throws Exception{
		int result = 0;
		requestDocDto.setRegisterNo(SecurityUtil.getMemberNo());
		requestDocDto.setRequestDocGroupNo(requestDocGroupNo);

		// 1. 삭제된 파일이 있는지 확인.
		if(requestDocDto.getFileDel() != null && !"".equals(requestDocDto.getFileDel())){
			fileMapper.deleteFileArr(requestDocDto.getFileDel());
		}

		result = frontRequestMapper.updateRequestDocGroup(requestDocDto);
		if(result > 0){
			frontRequestMapper.updateRequestDoc(requestDocDto);

			if(!"".equals(requestDocDto.getTypeList())){
				ObjectMapper objectMapper = new ObjectMapper();
				List<RequestTypeDto> typeList;

				// 기존 typeList 삭제
				frontRequestMapper.deleteRequestType(requestDocDto);
				try {
					String jsonString = requestDocDto.getTypeList();
					typeList = objectMapper.readValue(jsonString, new TypeReference<List<RequestTypeDto>>(){});
					if(typeList.size() > 0){
						for (int i = 0; i < typeList.size(); i++) {
							typeList.get(i).setRegisterNo(SecurityUtil.getMemberNo());
							typeList.get(i).setRequestDocNo(requestDocDto.getRequestDocNo());
							frontRequestMapper.insertRequestType(typeList.get(i));
						}
					}
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}
			if(files != null){
				for (int i = 0; i < files.size(); i++) {
					S3FileVO fileVO = s3Upload.uploadToEncrypt(files.get(i));
					fileVO.setFileFromNo(requestDocDto.getRequestDocGroupNo());
					fileVO.setFileSe("G");
					fileVO.setRegisterNo(SecurityUtil.getMemberNo());
					fileMapper.insertFile(fileVO);
				}
			}
		}

		return requestDocDto.getRequestDocGroupNo();
	}

	public int putRequestDetail(Integer requestDocGroupNo, RequestDocDto requestDocDto, List<MultipartFile> files) throws Exception{
		int result = 0;
		requestDocDto.setRegisterNo(SecurityUtil.getMemberNo());
		requestDocDto.setRequestDocGroupNo(requestDocGroupNo);

		if(requestDocDto.getFileDel() != null && !"".equals(requestDocDto.getFileDel())){
			fileMapper.deleteFileArr(requestDocDto.getFileDel());
		}

		result = frontRequestMapper.updateRequestDocGroup(requestDocDto);
		if(result > 0){
			ObjectMapper objectMapper = new ObjectMapper();
			List<RequestDocListDto> docList;
			RequestDocListDto docListDto;

			String json = requestDocDto.getDocList();
			docList = objectMapper.readValue(json, new TypeReference<List<RequestDocListDto>>(){});

			if(docList != null){
				if(docList.size() > 0){
					for (int j = 0; j < docList.size(); j++) {
						docList.get(j).setRequestDocGroupNo(requestDocDto.getRequestDocGroupNo());
						docList.get(j).setRegisterNo(SecurityUtil.getMemberNo());

						if(docList.get(j).getStatus() == null){
							frontRequestMapper.insertRequestDocDetail(docList.get(j));
						}else{
							if("U".equals(docList.get(j).getStatus())){
								frontRequestMapper.updateRequestDocDetail(docList.get(j));
							}else if("D".equals(docList.get(j).getStatus())){
								frontRequestMapper.deleteRequestDocDetail(docList.get(j));
							}
						}

						String interestYn = docList.get(j).getInterestYn();
						if("Y".equals(interestYn)){
							ValueDto valueDto = new ValueDto();
							valueDto.setValueCn(docList.get(j).getValueSj());
							valueDto.setMemberNo(SecurityUtil.getMemberNo());
							int valueResult = frontRequestMapper.insertRequestValue(valueDto);
							if(valueResult > 0){
								ValueDataDto valueDataDto = new ValueDataDto();
								valueDataDto.setValueNo(valueDto.getValueNo());
								valueDataDto.setCementGapValue(docList.get(j).getCementGapValue());
								valueDataDto.setExtraGapValue(docList.get(j).getExtraGapValue());
								valueDataDto.setOcclusalDistanceValue(docList.get(j).getOcclusalDistanceValue());
								valueDataDto.setApproximalDistanceValue(docList.get(j).getApproximalDistanceValue());
								valueDataDto.setHeightMinimalValue(docList.get(j).getHeightMinimalValue());
								frontRequestMapper.insertRequestValueData(valueDataDto);
							}
						}

						if(requestDocDto.getTypeList() != null){
							// 기존 typeList 삭제
							frontRequestMapper.deleteRequestTypeDetail(docList.get(j));

							RequestTypeDetailDto typeDto = docList.get(j).getTypeDetail();
							if(typeDto != null){
								List<RequestTypeDentalDto> dentalList;
								RequestTypeDentalDto dentalDto;

								typeDto.setRegisterNo(SecurityUtil.getMemberNo());
								typeDto.setRequestDocNo(docList.get(j).getRequestDocNo());
								frontRequestMapper.insertRequestTypeDetail(typeDto);

								if(typeDto.getDentalList() != null){
									// 기존 dentalList 삭제
									frontRequestMapper.deleteRequestDentalDetail(typeDto);

									if(typeDto.getDentalList().size() > 0){
										for (int k = 0; k < typeDto.getDentalList().size(); k++) {
											typeDto.getDentalList().get(k).setRegisterNo(SecurityUtil.getMemberNo());
											typeDto.getDentalList().get(k).setRequestTypeNo(typeDto.getRequestTypeNo());

											frontRequestMapper.insertRequestTypeDental(typeDto.getDentalList().get(k));
										}
									}
								}
							}
						}
					}
				}
			}
			if(files != null){
				for (int i = 0; i < files.size(); i++) {
					S3FileVO fileVO = s3Upload.uploadToEncrypt(files.get(i));
					fileVO.setFileFromNo(requestDocDto.getRequestDocGroupNo());
					fileVO.setFileSe("G");
					fileVO.setRegisterNo(SecurityUtil.getMemberNo());
					fileMapper.insertFile(fileVO);
				}
			}
		}

		return requestDocDto.getRequestDocGroupNo();
	}

	public ProfileVo getRequestProfile(Integer memberNo) {
		return frontRequestMapper.selectRequestProfile(memberNo);
	}

	public RequestJsonVo getRequestJsonView(Integer requestDocGroupNo, Integer requestFormNo) {
		RequestJsonVo requestJsonVo = new RequestJsonVo();
		requestJsonVo = frontRequestMapper.selectRequestJsonView(requestDocGroupNo, requestFormNo, SecurityUtil.getMemberNo());
		if(requestJsonVo != null){
			requestJsonVo.setFileList(frontRequestMapper.selectRequestDocFileList(requestDocGroupNo));
			requestJsonVo.setKeyList(frontRequestMapper.selectRequestDocKeyList(requestDocGroupNo));
		}
		return requestJsonVo;
	}
}

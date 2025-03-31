package com.dentner.front.api.common;

import com.dentner.core.cmmn.dto.*;
import com.dentner.core.cmmn.mapper.CommonMapper;
import com.dentner.core.cmmn.service.FileUploadService;
import com.dentner.core.cmmn.service.MailService;
import com.dentner.core.cmmn.service.S3Upload;
import com.dentner.core.cmmn.vo.*;
import com.dentner.core.util.EmailUtil;
import com.dentner.core.util.PhoneUtil;
import com.dentner.core.util.SecurityUtil;
import com.dentner.core.util.TextUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.popbill.api.KakaoService;
import com.popbill.api.PopbillException;
import com.popbill.api.kakao.KakaoButton;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommonService {

	@Value("${nhn-sms.url}")
	private String smsUrl;

	@Value("${nhn-sms.app-key}")
	private String smsAppKey;

	@Value("${nhn-sms.secret-key}")
	private String smsSecretKey;

	@Value("${nhn-sms.send-no}")
	private String smsSendNo;

	@Autowired
	private WebClient webClient;

	@Autowired
	CommonMapper commonMapper;

	@Autowired
	private MailService mailService;

	@Autowired
	private KakaoService kakaoService;

	@Autowired
	private FileUploadService fileUploadService;

	@Value("${innopay.mid}")
	private String mid;
	
	@Value("${innopay.license-key}")
	private String licenseKey;

	private final S3Upload s3Upload;

	@Value("${popbill.corpNum}")
	private String CorpNum;

	@Value("${popbill.senderNum}")
	private String senderNum;

	@Value("${popbill.userID}")
	private String userID;

	public MailDto postMail(MailDto mailDto) {
		try {
			// 4. 메일 보내기
			String emailTemplate = EmailUtil.readHTMLTemplate("reply.html");
			System.out.println("emailTemplate = " + emailTemplate);
			mailDto.setMailTo("gbajsl@gmail.com");
			mailDto.setMailSubject("test");
			mailDto.setMailContent(emailTemplate);

			mailService.mailSend(mailDto);
		}catch (Exception e){
		}
		return mailDto;
	}
	
	/**
	 * 계좌인증
	 * @param memberAddDto
	 * @return
	 * @throws Exception 
	 */
	public String postAccCheck(MemberBankDto memberBankDto) throws Exception {
		String apiUrl = "https://acct.innopay.co.kr/AcctNmReq.acct";
		
		String moId = TextUtil.generateTxId(15);
		
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedNow = now.format(formatter);
		
        
        String bankCode = memberBankDto.getMemberAccountBankNo();
        String accountNumber = memberBankDto.getMemberAccountNumber();
        //String AccountNumber = memberAddDto.getMemberAccountNumber(); 
        String accountName = memberBankDto.getMemberAccountName();
		String idNo = memberBankDto.getMemberIdNo();

		Map<String, Object> body = new HashMap<String, Object>();
		body.put("mid", mid); // MID
		body.put("merkey", licenseKey);
		body.put("moid", moId); // 가맹점 주문번호
		body.put("req_dt", formattedNow); // 요청일시
		body.put("bankCode", bankCode); // 은행코드
		body.put("acntNo", accountNumber); // 계좌번호
		body.put("idNo", idNo); // 사업자번호(10자리) 또는 생년월일(6자리, YYMMDD)
		body.put("acntNm", accountName); // 입금계좌 예금주명
		
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(body);
		
		
		HttpEntity entity = new StringEntity(json, "UTF-8");

		HttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(apiUrl);
		httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8");
		httpPost.setEntity(entity);

		HttpResponse response = httpClient.execute(httpPost);
		HttpEntity result = response.getEntity();

		if (result != null) {
			String resultValue = EntityUtils.toString(result);
			Map<String, Object> resultMap = new HashMap<String, Object>();
			
			ObjectMapper jsonMapper = new JsonMapper();
			resultMap = jsonMapper.readValue(resultValue, Map.class);
			if("0000".equals(resultMap.get("resultCode"))){
				// 암호화
//				CardAddDto cardAddDto = new CardAddDto();
//				cardAddDto.setCardNumber(SecurityUtil.encrypt(cardNumber));	// 카드 번호
//				cardAddDto.setCardMonth(SecurityUtil.encrypt(cardExpire[0]));	// 유효기간 연
//				cardAddDto.setCardYear(SecurityUtil.encrypt(cardExpire[1]));		// 유효기간 월
//				cardAddDto.setCardCompanyNo(Integer.parseInt(resultMap.get("cardCode").toString()));	// 카드사 no
//				cardAddDto.setCardPassword(SecurityUtil.encrypt(cardPassword));	// 카드 비밀번호
//				cardAddDto.setIdNumType(idNumType);
//				cardAddDto.setMoid(moId);
//				cardAddDto.setBuyerName(buyerName);
//				cardAddDto.setUserid(userEmail);
//				cardAddDto.setIdNum(SecurityUtil.encrypt(idNum));
//				cardAddDto.setBillKey(SecurityUtil.encrypt(resultMap.get("billKey").toString()));
//				cardAddDto.setRegisterNo(SecurityUtil.getMemberNo());
//
//				resultCnt = mileageMapper.insertCard(cardAddDto);
			}else{
				throw new Exception(resultMap.get("resultMsg").toString());
			}
		}
		
		return "";
	}

	@Transactional
	public FileVO postEditFile(FileDto fileDto) throws IOException {
		FileVO fileVO = fileUploadService.uploadToBase64(fileDto.getFileName(), fileDto.getFileBase64(), "editor");
		return fileVO;
    }

	public List<CodeVo> getCodeList(Integer parentNo, String type) {
		return commonMapper.selectCodeList(parentNo, type);
	}

	public AuthCodeDto postAuthPhone(PhoneDto phoneDto) throws Exception{
		String phone = phoneDto.getPhone();
		String certification =  phoneDto.getCertification();

		if(!"C".equals(certification)){
			// 1. 핸드폰번호가 있는지 확인.
			int result = commonMapper.selectUserPhone(phoneDto);

			if(result == 0 ){
				throw new Exception("일치하는 회원정보가 없습니다.");
			}
		}
		// 2. 인증번호 생성
		String authCode = PhoneUtil.generateOTP(6);
		String token = UUID.randomUUID().toString();

		// 3. DB에 TOKEN 저장
		AuthCodeDto authCodeDto = new AuthCodeDto();
		authCodeDto.setAuthCode(authCode);
		authCodeDto.setTokenValue(token);
		authCodeDto.setPhoneNo(phone);
		authCodeDto.setMemberContactNation(phoneDto.getMemberContactNation());

		int authResult = commonMapper.insertAuthCode(authCodeDto);

		if(authResult == 0){
			throw new Exception("일시적인 오류가 발생했습니다.");
		}

		// 4. SMS 보내기
		ObjectMapper jsonMapper = new JsonMapper();
		Map<String, Object> resultMap = new HashMap<String, Object>();

		String smsMsg = "[덴트너] 인증번호 ["+authCode+"]를 입력해주세요. ";
		URI uri = UriComponentsBuilder.fromUriString(smsUrl+"/sms/v3.0/appKeys/"+smsAppKey+"/sender/sms")
				.build(true)
				.toUri();

		Map<String, Object> bodyMap = new HashMap<>();
		bodyMap.put("body", smsMsg);
		bodyMap.put("sendNo", smsSendNo);

		Map<String, String> recipient = new HashMap<>();
		recipient.put("recipientNo", phone);
		recipient.put("countryCode", commonMapper.selectNationCode(phoneDto.getMemberContactNation()));
		bodyMap.put("recipientList", new Map[]{recipient});

		// Convert Map to JSON string
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonBody = objectMapper.writeValueAsString(bodyMap);
		System.out.println("jsonBody = " + jsonBody);
		String resultValue = webClient
				.mutate()
				.defaultHeader("X-Secret-Key", smsSecretKey)
				.defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
				.build()
				.post()
				.uri(uri)
				.bodyValue(jsonBody)
				.retrieve()
				.bodyToMono(String.class)
				.block();


		resultMap = jsonMapper.readValue(resultValue, Map.class);

		Map<String, Object> headerMap =  (Map) resultMap.get("header");
		String isSuccessful = headerMap.get("isSuccessful").toString();
		if("false".equals(isSuccessful)){
			throw new Exception("SMS 발송에 실패하였습니다.");
		}
		System.out.println("authCode = " + authCode);
		authCodeDto.setAuthCode("");
		authCodeDto.setPhoneNo("");


		return authCodeDto;
	}

	public int getAuthPhone(String authCode, String token) throws Exception{
		int result = 1;
		PhoneVo phoneVo = new PhoneVo();

		phoneVo = commonMapper.selectAuthPhone(authCode, token);
		if(phoneVo == null){
			result = 0;
			throw new Exception("인증번호가 일치하지 않습니다.");
		}
		// 인증 여부 업데이트
		commonMapper.updateAuthPhone(authCode, token);
		return result;
	}

	public List<TeethTypeVo> getTeethType(Integer parentNo, String type) {
		return commonMapper.selectTeethType(parentNo, type);
	}

	public int getAlarmCnt() {
		return commonMapper.selectAlarmCnt(SecurityUtil.getMemberNo());
	}

	public AlarmListVo getAlarmList(AlarmDto alarmDto) {
		alarmDto.setMemberNo(SecurityUtil.getMemberNo());

		AlarmListVo alarmListVo = new AlarmListVo();
		alarmListVo.setList(commonMapper.selectAlarmList(alarmDto));
		alarmListVo.setCnt(commonMapper.selectAlarmListCnt(alarmDto));
		return alarmListVo;
	}

	public int putAlarmReadAll(String type) {
		return commonMapper.updateAlarmReadAll(SecurityUtil.getMemberNo(), type);
	}

	public int putAlarmRead(Integer alarmNo) {
		return commonMapper.updateAlarmRead(SecurityUtil.getMemberNo(), alarmNo);
	}

	public S3FileVO postFile(MultipartFile file) throws Exception{
		S3FileVO fileVO = null;
		if(file != null){
			fileVO = s3Upload.upload(file);
		}
		return fileVO;
	}

	public int postAlarm(AlarmAddDto alarmAddDto) {
		return commonMapper.insertAlarm(alarmAddDto);
	}

	public int sendKaKaoSend(AlarmTalkDto alarmTalkDto) {
		int result = 0;
		/**
		 * 승인된 템플릿의 내용을 작성하여 1건의 알림톡 전송을 팝빌에 접수합니다.
		 * - 사전에 승인된 템플릿의 내용과 알림톡 전송내용(content)이 다를 경우 전송실패 처리됩니다.
		 * - 전송실패 시 사전에 지정한 변수 'altSendType' 값으로 대체문자를 전송할 수 있고 이 경우 문자(SMS/LMS) 요금이 과금됩니다.
		 * - https://developers.popbill.com/reference/kakaotalk/java/api/send#SendATSOne
		 */

		// 승인된 알림톡 템플릿코드
		// └ 알림톡 템플릿 관리 팝업 URL(GetATSTemplateMgtURL API) 함수, 알림톡 템플릿 목록 확인(ListATStemplate API) 함수를 호출하거나
		//   팝빌사이트에서 승인된 알림톡 템플릿 코드를  확인 가능.
		// "022070000338";
		String templateCode = alarmTalkDto.getTemplateCode();

		// altSendType = 'C' / 'A' 일 경우, 대체문자를 전송할 발신번호
		// altSendType = '' 일 경우, null 또는 공백 처리
		// ※ 대체문자를 전송하는 경우에는 사전에 등록된 발신번호 입력 필수

		// 알림톡 내용 (최대 1000자)
		String content = alarmTalkDto.getContent();
//        String content = "[ 팝빌 ]\n"
//                + "신청하신 #{템플릿코드}에 대한 심사가 완료되어 승인 처리되었습니다.\n"
//                + "해당 템플릿으로 전송 가능합니다.\n\n"
//                + "문의사항 있으시면 파트너센터로 편하게 연락주시기 바랍니다.\n\n"
//                + "팝빌 파트너센터 : 1600-8536\n"
//                + "support@linkhub.co.kr";

		// 대체문자 제목
		// - 메시지 길이(90byte)에 따라 장문(LMS)인 경우에만 적용.
		String altSubject = "";

		// 대체문자 유형(altSendType)이 "A"일 경우, 대체문자로 전송할 내용 (최대 2000byte)
		// └ 팝빌이 메시지 길이에 따라 단문(90byte 이하) 또는 장문(90byte 초과)으로 전송처리
		String altContent = "";

		// 대체문자 유형 (null , "C" , "A" 중 택 1)
		// null = 미전송, C = 알림톡과 동일 내용 전송 , A = 대체문자 내용(altContent)에 입력한 내용 전송
		String altSendType = "C";

		// 수신번호
		String receiverNum = alarmTalkDto.getReceiverNum();

		// 수신자명
		String receiverName = "";

		// 예약전송일시, 형태(yyyyMMddHHmmss)
		// - 분단위 전송, 미입력 시 즉시 전송
		String sndDT = "";

		// 전송요청번호
		// 팝빌이 접수 단위를 식별할 수 있도록 파트너가 할당한 식별번호.
		// 1~36자리로 구성. 영문, 숫자, 하이픈(-), 언더바(_)를 조합하여 팝빌 회원별로 중복되지 않도록 할당.
		String requestNum = "";

		// 알림톡 버튼정보를 템플릿 신청시 기재한 버튼정보와 동일하게 전송하는 경우 null 처리.
		KakaoButton[] btns = null;

		// 알림톡 버튼 URL에 #{템플릿변수}를 기재한경우 템플릿변수 영역을 변경하여 버튼정보 구성
		// KakaoButton[] btns = new KakaoButton[1];

		KakaoButton button = new KakaoButton();
		// button.setN("버튼명"); // 버튼명
		// button.setT("WL"); // 버튼타입
		// button.setU1("https://www.popbill.com"); // 버튼링크1
		// button.setU2("http://test.popbill.com"); // 버튼링크2
		// button.setTg("out"); // 아웃링크
		// btns[0] = button;

		try {
			String receiptNum = kakaoService.sendATS(CorpNum, templateCode, senderNum, content, altSubject, altContent, altSendType,
					receiverNum, receiverName, sndDT, userID, requestNum, btns);
			result = 1;
		} catch (PopbillException e) {
			e.printStackTrace();
			result = 0;
		}
		return result;
	}

	public AlarmTalkDto selectRequestInfo(Integer requestFormNo) {
		return commonMapper.selectRequestInfo(requestFormNo);
	}

	public MainStatVo getMainStatData() {
		return commonMapper.selectMainStatData();
	}
	
	public String getMemberNickName(String memberNo) {
		return commonMapper.selectMemberNickName(memberNo);
	}

	public AlarmTalkDto selectEstimateInfo(Integer requestFormNo, Integer registerNo) {
		return commonMapper.selectEstimateInfo(requestFormNo, registerNo);
	}


	public AlarmTalkDto selectMileageInfo(int memberNo) {
		return commonMapper.selectMileageInfo(memberNo);
	}
	
	public int selectAlarm(int memberNo, int alarmCodeNo) {
		/**
		 * alarmCodeNo 
		 * 치자이너 치기공소 9, 의뢰인 2 - 댓글알림 
		 * 의뢰인 3 - 견적알림
		 * 치자이너 10, 의뢰인 4 - 거래알림
		 * 치자이너 11, 의뢰인 5 - 마일리지알림
		 * 치자이너 12, 의뢰인 6 - 채팅
		 */
		return commonMapper.selectAlarm(memberNo, alarmCodeNo);
	}
	
	public int selectAlarm(int memberNo, List<String> alarmCodeList) {
		/**
		 * alarmCodeNo 
		 * 치자이너 치기공소 9, 의뢰인 2 - 댓글알림 
		 * 의뢰인 3 - 견적알림
		 * 치자이너 10, 의뢰인 4 - 거래알림
		 * 치자이너 12, 의뢰인 6 - 채팅
		 */
		return commonMapper.selectAlarmCheckList(memberNo, alarmCodeList);
	}

	@Async
	public void postFCMPush(int memberNo, PushDto pushDto, String goUrl) {
		List<String> token = commonMapper.selectMemberFCMToken(memberNo);
		if(token != null && token.size() > 0){
			for (int i = 0; i < token.size(); i++) {
				Message message = Message.builder()
						.putData("title", "Dentner")
						.putData("body", pushDto.getBody())
						.putData("url", goUrl)
						.setToken(token.get(i))
						.build();
				try {
					String response = FirebaseMessaging.getInstance().send(message);
					System.out.println("Successfully sent message: " + response);
					} catch (Exception e) {
						//
					}
				}
			}
		}

	@Async
	public void postPush(PushDto pushDto) throws Exception{
		List<String> token = commonMapper.selectMemberFCMToken(pushDto.getMemberNo());
		if(token != null && token.size() > 0){
			System.out.println("pushDto.getUrl() = "  + pushDto.getUrl());
			for (int i = 0; i < token.size(); i++) {
				Message message = Message.builder()
						.putData("title", "Dentner")
						.putData("body", pushDto.getBody())
						.putData("url", pushDto.getUrl())
						.setToken(token.get(i))
						.build();
				try {
					String response = FirebaseMessaging.getInstance().send(message);
					System.out.println("Successfully sent message: " + response);
				} catch (Exception e) {
					//
				}
			}
		}
	}

	public String selectMileageAmount(int mileageNo) {
		return commonMapper.selectMileageAmount(mileageNo);
	}
	
	public String selectMileage(int mileageNo) {
		return commonMapper.selectMileage(mileageNo);
	}

	public String selectMemberTp(int memberNo) {
		return commonMapper.selectMemberTp(memberNo);
	}
}

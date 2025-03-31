package com.dentner.front.api.mileage;

import com.dentner.core.cmmn.dto.*;
import com.dentner.core.cmmn.mapper.MileageMapper;
import com.dentner.core.cmmn.vo.*;
import com.dentner.core.util.*;
import com.dentner.front.api.common.CommonService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.annotation.Resource;
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
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MileageService {

	@Autowired
	MileageMapper mileageMapper;

	@Autowired
	private WebClient webClient;

	@Value("${exchange-rate}")
	private int exchangeAmount;

	@Value("${innopay.mid}")
	private String mid;

	@Resource(name= "commonService")
	CommonService commonService;

	public CardVo getCard() {
		CardVo cardVo = new CardVo();
		cardVo = mileageMapper.selectCardInfo(SecurityUtil.getMemberNo());
		if(cardVo != null){
			String cardNumber = SecurityUtil.decrypt(cardVo.getCardNumber());
			String idNumType = cardVo.getIdNumType();
			String idNum = SecurityUtil.decrypt(cardVo.getIdNum());
			if("1".equals(idNumType)){
				idNum = idNum.replaceAll("(\\d{3})(\\d{2})(\\d{5})", "$1-$2-$3");
			}
			cardVo.setCardNumber(cardNumber.replaceAll("(.{4})", "$1-").replaceAll("-$", ""));
			cardVo.setCardMonth(SecurityUtil.decrypt(cardVo.getCardMonth()));
			cardVo.setCardYear(SecurityUtil.decrypt(cardVo.getCardYear()));
			cardVo.setCardPassword(SecurityUtil.decrypt(cardVo.getCardPassword()));
			cardVo.setIdNum(idNum);
			cardVo.setBillKey("");
		}
		return cardVo;
	}

	public int postCard(String str) throws Exception{
		int resultCnt = 0;
		String encryptedString = URLDecoder.decode(str, "UTF-8");
		if (encryptedString.startsWith("\"") && encryptedString.endsWith("\"")) {
			encryptedString = encryptedString.substring(1, encryptedString.length() - 1);
		}
		ObjectMapper jsonMapper = new JsonMapper();
		Map<String, Object> resultMap = new HashMap<String, Object>();

		// api body
		// {"mid":"arstest03m","moid":"20240828001","buyerName":"테스트","billKey":"","cardNum":"","cardExpire":"","cardPwd":"","idNum":"","userId":"","arsUseYn":"N"}
		String apiUrl = "https://api.innopay.co.kr/api/regAutoCardBill";


		// {"cardNumber":"1111-1111-1111-1111","idNum":"111111","cardPassword":"11","cardExpire":"11/11","idNumType":0}
		// idNumType 인증수단 : 0 주민번호, 1 사업자번호
		// idNum 인증번호
		// cardExpire 유효기간 : MMYY
		String decryptedString = AesCryptUtil.decrypt(encryptedString);

		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, Object> map = objectMapper.readValue(decryptedString, new TypeReference<Map<String, Object>>() {});

		int cnt = mileageMapper.selectCardCnt(SecurityUtil.getMemberNo());
		if(cnt > 0){
			throw new Exception("이미 등록된 카드가 있습니다.");
		}

		String cardNumber = map.get("cardNumber").toString().replaceAll("-", "");
		String[] cardExpire = map.get("cardExpire").toString().split("/");
		String cardPassword = map.get("cardPassword").toString();
		String idNum = map.get("idNum").toString().replaceAll("-","");
		String idNumType = map.get("idNumType").toString();
		String moId = TextUtil.generateTxId(15);
		String buyerName = map.get("buyerName").toString();
		String userEmail = map.get("userEmail").toString();

		Map<String, Object> body = new HashMap<String, Object>();
		body.put("mid", mid); // MID
		body.put("moid", moId); // 가맹점 주문번호
		body.put("buyerName", buyerName); // 등록자 이름
		body.put("billKey", ""); // 기존 발급받은 빌키(해당 값이 존재할 경우 기존 빌키를 삭제 후 새로 등록)
		body.put("cardNum", cardNumber); // 카드번호
		body.put("cardExpire", cardExpire[1]+cardExpire[0]); // 유효기간 YYMM
		body.put("cardPwd", cardPassword); // 앞 비밀번호 2자리
		body.put("idNum", idNum); // 인증번호
		body.put("userId", userEmail); // 등록자 고유 아이디
		body.put("arsUseYn", "N"); // 자동결제 등록 ARS 사용 여부

		String json = objectMapper.writeValueAsString(body);

		System.out.println("json====>>"+json);

		HttpEntity entity = new StringEntity(json, "UTF-8");

		HttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(apiUrl);
		httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8");
		httpPost.setEntity(entity);

		HttpResponse response = httpClient.execute(httpPost);
		HttpEntity result = response.getEntity();

		if (result != null) {
			// BIKYtestpay01m2408281526048250
			String resultValue = EntityUtils.toString(result);
			resultMap = jsonMapper.readValue(resultValue, Map.class);
			// resultMap = {resultCode=0000, resultMsg=빌키 발급 성공, payExpDate=, arsTid=2024082815233619343, arsNo=, arsOrderKey=, moid=20240828001, userId=TEST, billKey=BIKYtestpay01m2408281523368907, cardCode=06, cardCl=1}
			System.out.println("resultMap = " + resultMap.get("billKey"));
			System.out.println("resultMap = " + resultMap.get("cardCode"));
			if("0000".equals(resultMap.get("resultCode"))){
				// 암호화
				CardAddDto cardAddDto = new CardAddDto();
				cardAddDto.setCardNumber(SecurityUtil.encrypt(cardNumber));	// 카드 번호
				cardAddDto.setCardMonth(SecurityUtil.encrypt(cardExpire[0]));	// 유효기간 연
				cardAddDto.setCardYear(SecurityUtil.encrypt(cardExpire[1]));		// 유효기간 월
				cardAddDto.setCardCompanyNo(Integer.parseInt(resultMap.get("cardCode").toString()));	// 카드사 no
				cardAddDto.setCardPassword(SecurityUtil.encrypt(cardPassword));	// 카드 비밀번호
				cardAddDto.setIdNumType(idNumType);
				cardAddDto.setMoid(moId);
				cardAddDto.setBuyerName(buyerName);
				cardAddDto.setUserid(userEmail);
				cardAddDto.setIdNum(SecurityUtil.encrypt(idNum));
				cardAddDto.setBillKey(SecurityUtil.encrypt(resultMap.get("billKey").toString()));
				cardAddDto.setRegisterNo(SecurityUtil.getMemberNo());

				resultCnt = mileageMapper.insertCard(cardAddDto);
			}else{
				throw new Exception(resultMap.get("resultMsg").toString());
			}
		}
		return resultCnt;
	}

	public int putCard(String str) throws Exception{

		int resultCnt = 0;
		String encryptedString = URLDecoder.decode(str, "UTF-8");
		if (encryptedString.startsWith("\"") && encryptedString.endsWith("\"")) {
			encryptedString = encryptedString.substring(1, encryptedString.length() - 1);
		}
		ObjectMapper jsonMapper = new JsonMapper();
		Map<String, Object> resultMap = new HashMap<String, Object>();

		// {"mid":"arstest03m","moid":"20240828001","buyerName":"테스트","billKey":"","cardNum":"","cardExpire":"","cardPwd":"","idNum":"","userId":"","arsUseYn":"N"}
		String apiUrl = "https://api.innopay.co.kr/api/regAutoCardBill";

		// {"cardNumber":"1111-1111-1111-1111","idNum":"111111","cardPassword":"11","cardExpire":"11/11","idNumType":0}
		// idNumType 인증수단 : 0 주민번호, 1 사업자번호
		// idNum 인증번호
		// cardExpire 유효기간 : MMYY
		String decryptedString = AesCryptUtil.decrypt(encryptedString);

		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, Object> map = objectMapper.readValue(decryptedString, new TypeReference<Map<String, Object>>() {});

		CardVo cardVo = new CardVo();
		cardVo = mileageMapper.selectCardInfo(SecurityUtil.getMemberNo());
		if(cardVo != null){
			cardVo.setBillKey(SecurityUtil.decrypt(cardVo.getBillKey()));
		}

		System.out.println("map = " + map.toString());
		String cardNumber = map.get("cardNumber").toString().replaceAll("-", "");
		String[] cardExpire = map.get("cardExpire").toString().split("/");
		String cardPassword = map.get("cardPassword").toString();
		String idNum = map.get("idNum").toString().replaceAll("-","");
		String idNumType = map.get("idNumType").toString();
		String moId = TextUtil.generateTxId(15);
		String buyerName = map.get("buyerName").toString();
		String userEmail = map.get("userEmail").toString();

		Map<String, Object> body = new HashMap<String, Object>();
		body.put("mid", mid); // MID
		body.put("moid", moId); // 가맹점 주문번호
		body.put("buyerName", buyerName); // 등록자 이름
		body.put("billKey", cardVo.getBillKey()); // 기존 발급받은 빌키(해당 값이 존재할 경우 기존 빌키를 삭제 후 새로 등록)
		body.put("cardNum", cardNumber); // 카드번호
		body.put("cardExpire", cardExpire[1]+cardExpire[0]); // 유효기간 YYMM
		body.put("cardPwd", cardPassword); // 앞 비밀번호 2자리
		body.put("idNum", idNum); // 인증번호
		body.put("userId", userEmail); // 등록자 고유 아이디
		body.put("arsUseYn", "N"); // 자동결제 등록 ARS 사용 여부

		String json = objectMapper.writeValueAsString(body);

		HttpEntity entity = new StringEntity(json, "UTF-8");

		HttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(apiUrl);
		httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8");
		httpPost.setEntity(entity);

		HttpResponse response = httpClient.execute(httpPost);
		HttpEntity result = response.getEntity();

		if (result != null) {
			// BIKYtestpay01m2408281526048250
			String resultValue = EntityUtils.toString(result);
			resultMap = jsonMapper.readValue(resultValue, Map.class);
			// resultMap = {resultCode=0000, resultMsg=빌키 발급 성공, payExpDate=, arsTid=2024082815233619343, arsNo=, arsOrderKey=, moid=20240828001, userId=TEST, billKey=BIKYtestpay01m2408281523368907, cardCode=06, cardCl=1}
			if("0000".equals(resultMap.get("resultCode"))){
				// 암호화
				CardAddDto cardAddDto = new CardAddDto();
				cardAddDto.setCardNumber(SecurityUtil.encrypt(cardNumber));	// 카드 번호
				cardAddDto.setCardMonth(SecurityUtil.encrypt(cardExpire[0]));	// 유효기간 연
				cardAddDto.setCardYear(SecurityUtil.encrypt(cardExpire[1]));		// 유효기간 월
				cardAddDto.setCardCompanyNo(Integer.parseInt(resultMap.get("cardCode").toString()));	// 카드사 no
				cardAddDto.setCardPassword(SecurityUtil.encrypt(cardPassword));	// 카드 비밀번호
				cardAddDto.setIdNumType(idNumType);
				cardAddDto.setMoid(moId);
				cardAddDto.setBuyerName(buyerName);
				cardAddDto.setUserid(userEmail);
				cardAddDto.setIdNum(SecurityUtil.encrypt(idNum));
				cardAddDto.setBillKey(SecurityUtil.encrypt(resultMap.get("billKey").toString()));
				cardAddDto.setRegisterNo(SecurityUtil.getMemberNo());

				resultCnt = mileageMapper.updateCard(cardAddDto);
			}else{
				throw new Exception(resultMap.get("resultMsg").toString());
			}
		}
		return resultCnt;
//		cardAddDto.setRegisterNo(SecurityUtil.getMemberNo());
//		// 암호화
//		cardAddDto.setCardNumber(SecurityUtil.encrypt(cardAddDto.getCardNumber()));
//		cardAddDto.setCardCvc(SecurityUtil.encrypt(cardAddDto.getCardCvc()));
//		cardAddDto.setCardMonth(SecurityUtil.encrypt(cardAddDto.getCardMonth()));
//		cardAddDto.setCardYear(SecurityUtil.encrypt(cardAddDto.getCardYear()));
//		cardAddDto.setCardPassword(SecurityUtil.encrypt(cardAddDto.getCardPassword()));
//		cardAddDto.setCardName(SecurityUtil.encrypt(cardAddDto.getCardName()));
//
//		return mileageMapper.updateCard(cardAddDto);
	}

	public int postMileageCharge(MileageAddDto cardAddDto) throws Exception{
		CardVo cardVo = new CardVo();
		cardVo = mileageMapper.selectCardInfo(SecurityUtil.getMemberNo());
		if(cardVo != null){
			String cardNumber = SecurityUtil.decrypt(cardVo.getCardNumber());
			String idNumType = cardVo.getIdNumType();
			String idNum = SecurityUtil.decrypt(cardVo.getIdNum());
			if("1".equals(idNumType)){
				idNum = idNum.replaceAll("(\\d{3})(\\d{2})(\\d{5})", "$1-$2-$3");
			}
			cardVo.setCardNumber(cardNumber.replaceAll("(.{4})", "$1-").replaceAll("-$", ""));
			cardVo.setCardMonth(SecurityUtil.decrypt(cardVo.getCardMonth()));
			cardVo.setCardYear(SecurityUtil.decrypt(cardVo.getCardYear()));
			cardVo.setCardPassword(SecurityUtil.decrypt(cardVo.getCardPassword()));
			cardVo.setBillKey(SecurityUtil.decrypt(cardVo.getBillKey()));
			cardVo.setIdNum(idNum);
		}

		cardAddDto.setRegisterNo(SecurityUtil.getMemberNo());
		cardAddDto.setMileageCn("마일리지충전 " + cardAddDto.getMileageAmount());

		/*
		* {
				"mid": "arstest03m",
				"moid": "20240829001",
				"buyerName": "테스트",
				"goodsName": "테스트상품",
				"amt": "",
				"billKey": "",
				"userId": ""
			}
		* */

		ObjectMapper jsonMapper = new JsonMapper();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		URI uri = UriComponentsBuilder.fromUriString("https://api.innopay.co.kr/api/payAutoCardBill")
				.build(true)
				.toUri();
		String moId = TextUtil.generateTxId(15);

		Map<String, Object> bodyMap = new HashMap<>();
		bodyMap.put("mid", mid);
		bodyMap.put("moid", moId);
		bodyMap.put("buyerName", cardVo.getBuyerName());
		bodyMap.put("goodsName", "마일리지충전 " + cardAddDto.getMileageAmount());
		bodyMap.put("amt", cardAddDto.getMileageAmount());
		bodyMap.put("billKey", cardVo.getBillKey());
		bodyMap.put("userId", cardVo.getUserId());

		// Convert Map to JSON string
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonBody = objectMapper.writeValueAsString(bodyMap);
		System.out.println("jsonBody = " + jsonBody);
		String resultValue = webClient
				.mutate()
				.defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
				.build()
				.post()
				.uri(uri)
				.bodyValue(jsonBody)
				.retrieve()
				.bodyToMono(String.class)
				.block();


		resultMap = jsonMapper.readValue(resultValue, Map.class);
		int resultCode = 0;
		if("0000".equals(resultMap.get("resultCode").toString())){
			resultCode = 1;
			cardAddDto.setOrderNumber(moId);
			mileageMapper.insertMileageCharge(cardAddDto);
			
			int isAlarm = commonService.selectAlarm(SecurityUtil.getMemberNo(), 5);
            if (isAlarm > 0) {
				// 2025-03-05 cjj 로그인한 사람이 국내/해외 의뢰인인지 확인.
				// 회원유형 (A:한국인, B:외국인)
				String memberTp = commonService.selectMemberTp(SecurityUtil.getMemberNo());
				String msg = "";
				if("A".equals(memberTp)){
					msg = "마일리지충전";
				}else{
					msg = "Charge mileage";
				}
            	PushDto push = new PushDto();
                push.setBody(msg + " " + cardAddDto.getMileageAmount());
                commonService.postFCMPush(SecurityUtil.getMemberNo(), push, "/mileage");
                
                AlarmAddDto alarmAddDto = new AlarmAddDto();
				alarmAddDto.setAlarmSj(msg);
				alarmAddDto.setAlarmCn(msg + " " +cardAddDto.getMileageAmount());
				alarmAddDto.setAlarmSe("F");
				alarmAddDto.setAlarmUrl("");
				alarmAddDto.setMemberNo(SecurityUtil.getMemberNo());
				commonService.postAlarm(alarmAddDto);


            }
			
		}

		return resultCode;
	}

	public MileageListVo getMileageCharge(MileageDto mileageDto) {
		mileageDto.setRegisterNo(SecurityUtil.getMemberNo());

		MileageListVo mileageListVo = new MileageListVo();
		mileageListVo.setList(mileageMapper.selectMileageChargeList(mileageDto));
		mileageListVo.setCnt(mileageMapper.selectMileageChargeListCnt(mileageDto));

		return mileageListVo;
	}

	public MileageListVo getMileagePayment(MileageDto mileageDto) {
		mileageDto.setRegisterNo(SecurityUtil.getMemberNo());

		MileageListVo mileageListVo = new MileageListVo();
		mileageListVo.setList(mileageMapper.selectMileagePaymentList(mileageDto));
		mileageListVo.setCnt(mileageMapper.selectMileagePaymentListCnt(mileageDto));

		return mileageListVo;
	}

	public int getMileage() {
		return mileageMapper.selectMileage(SecurityUtil.getMemberNo());
	}

	public Map<String, Object> getMileageDesigner() {
		MileageCalculateAddDto mileageCalculateAddDto = new MileageCalculateAddDto();
		mileageCalculateAddDto.setRegisterNo(SecurityUtil.getMemberNo());
		List<MileageVo> mileageList = null;

		int mileageWon = 0;
		int mileageDollar = 0;
		int totalWon = 0;
		int totalDollar = 0;

		// 원화
		mileageCalculateAddDto.setMileageUnit("A");
		mileageList = mileageMapper.selectCalculateList(mileageCalculateAddDto);

		if(mileageList != null && mileageList.size() > 0){
			for (int i = 0; i < mileageList.size(); i++) {
				MileageVo mileageVo = mileageList.get(i);
				totalWon += mileageVo.getMileageAmount();
				mileageWon += CalculateUtil.calculateAmount("A",mileageVo.getMileageAmount(), exchangeAmount);
			}
		}

		mileageCalculateAddDto.setMileageUnit("B");
		mileageList = mileageMapper.selectCalculateList(mileageCalculateAddDto);

		if(mileageList != null && mileageList.size() > 0){
			for (int i = 0; i < mileageList.size(); i++) {
				MileageVo mileageVo = mileageList.get(i);
				totalWon += mileageVo.getMileageAmount() * exchangeAmount;
				mileageWon += CalculateUtil.calculateAmount("B",mileageVo.getMileageAmount(), exchangeAmount);
			}
		}
		Map<String, Object> map = mileageMapper.selectMileageDesigner(SecurityUtil.getMemberNo());
		map.put("expectedAmount", mileageWon + mileageDollar);
		map.put("totalAmount", totalWon + totalDollar);

		return map;
	}

	public int postMileageRefund(MileageRefundAddDto mileageRefundAddDto) {
		mileageRefundAddDto.setRegisterNo(SecurityUtil.getMemberNo());
		
		int isAlarm = commonService.selectAlarm(SecurityUtil.getMemberNo(), 5);
        if (isAlarm > 0) {
			// 2025-03-05 cjj 로그인한 사람이 국내/해외 의뢰인인지 확인.
			// 회원유형 (A:한국인, B:외국인)
			String memberTp = commonService.selectMemberTp(SecurityUtil.getMemberNo());
			String msg = "";
			if("A".equals(memberTp)){
				msg = "마일리지 환불 신청완료";
			}else{
				msg = "Mileage refund requested";
			}

        	String content = commonService.selectMileageAmount(mileageRefundAddDto.getMileageNo());
        	PushDto push = new PushDto();
        	
        	push.setBody(msg + " " + content);
        	commonService.postFCMPush(SecurityUtil.getMemberNo(), push, "/mileage");
        	
            
            AlarmAddDto alarmAddDto = new AlarmAddDto();
            alarmAddDto.setAlarmSj(msg);
            alarmAddDto.setAlarmCn(msg + " " + content);
            alarmAddDto.setAlarmSe("F");
            alarmAddDto.setAlarmUrl("");
            alarmAddDto.setMemberNo(SecurityUtil.getMemberNo());
            commonService.postAlarm(alarmAddDto);
        }
		
		return mileageMapper.insertMileageRefund(mileageRefundAddDto);
	}

	public Map<String, Object> getMileageDesignerCalculate() {
		Map<String, Object> calculate = new HashMap<String, Object>();
		calculate = mileageMapper.selectMileageDesignerCalculate(SecurityUtil.getMemberNo());
		//calculate.put("mileageWon", CalculateUtil.calculateAmount("A", Integer.parseInt(calculate.get("mileageWon").toString()), exchangeAmount));
		//calculate.put("mileageDollar", CalculateUtil.calculateAmount("B", Integer.parseInt(calculate.get("mileageDollar").toString()), exchangeAmount));
		return calculate;
	}

	public int postMileageCalculate(MileageCalculateAddDto mileageCalculateAddDto) {
		mileageCalculateAddDto.setRegisterNo(SecurityUtil.getMemberNo());
		int result = 0;
		String msg = "";
		if("A".equals(mileageCalculateAddDto.getCalculateSe())){
			// 전체 정산
			int amount = 0;
			List<MileageVo> mileageList = mileageMapper.selectCalculateAllList(mileageCalculateAddDto);
			if(mileageList != null && mileageList.size() > 0){
				// 2025-03-06 cjj
				// 전체 정산 시 그룹 테이블에 데이터를 넣는다.
				mileageMapper.insertMileageCalculateGroup(mileageCalculateAddDto);

				for (int i = 0; i < mileageList.size(); i++) {
					MileageVo mileageVo = mileageList.get(i);
					mileageCalculateAddDto.setMileageNo(mileageVo.getMileageNo());
					mileageCalculateAddDto.setMileageUnit(mileageVo.getMileageUnit());
					// 환율 계산 해야 함.
					// 계산식
					mileageCalculateAddDto.setCalculateAmount(CalculateUtil.calculateAmount(mileageVo.getMileageUnit(), mileageVo.getMileageAmount(), exchangeAmount));
					result = mileageMapper.updateMileageCalculate(mileageCalculateAddDto);
					result += mileageMapper.insertMileageCalculate(mileageCalculateAddDto);
					
					amount += mileageCalculateAddDto.getCalculateAmount();
				}

				mileageCalculateAddDto.setCalculateGroupAmount(amount);
				mileageMapper.updateMileageCalculateGroup(mileageCalculateAddDto);
				msg = amount + "￦";
			}
		}else{
			// 입금 내역에서 정산
			result = mileageMapper.updateMileageCalculate(mileageCalculateAddDto);
			if(result > 0){
				result += mileageMapper.insertMileageCalculate(mileageCalculateAddDto);
				
				String cn = commonService.selectMileage(mileageCalculateAddDto.getMileageNo());
				msg = mileageCalculateAddDto.getCalculateAmount() + "/" + cn;
			}
		}
		
		int isAlarm = commonService.selectAlarm(SecurityUtil.getMemberNo(), 11);
        if (isAlarm > 0) {
        	
        	// 알림톡을 보낸다.
        	AlarmTalkDto alarmTalkDto = new AlarmTalkDto();
        	alarmTalkDto = commonService.selectMileageInfo(SecurityUtil.getMemberNo());
        	alarmTalkDto.setTemplateCode(AlarmTalkEnum.ARCHITECT_PAYMENT_PROCESSING_COMPLETED.getCode());
        	alarmTalkDto.setReceiverNum(alarmTalkDto.getDesignerHp());
        	String content = AlarmTalkEnum.ARCHITECT_PAYMENT_PROCESSING_COMPLETED.getMessageTemplate();
        	String message = content.replace("#{치과기공소or치자이너}", alarmTalkDto.getDesignerNickName());
        	alarmTalkDto.setContent(message);
        	result = commonService.sendKaKaoSend(alarmTalkDto);

        	// FCM
        	PushDto push = new PushDto();
            push.setBody("마일리지 정산 신청 완료  " + msg);
            commonService.postFCMPush(SecurityUtil.getMemberNo(), push, "/mileageOffice");
            
            // 알림
            AlarmAddDto alarmAddDto = new AlarmAddDto();
            alarmAddDto.setAlarmSj("마일리지 정산 신청 완료");
            alarmAddDto.setAlarmCn("마일리지 정산 신청 완료 " + msg);
            alarmAddDto.setAlarmSe("F");
            alarmAddDto.setAlarmUrl("");
            alarmAddDto.setMemberNo(SecurityUtil.getMemberNo());
            commonService.postAlarm(alarmAddDto);
        }

		return result;
	}

	public MileageDesignerListVo getMileageDeposit(MileageDto mileageDto) {
		mileageDto.setRegisterNo(SecurityUtil.getMemberNo());

		MileageDesignerListVo mileageDesignerListVo = new MileageDesignerListVo();
		List<MileageDesignerVo> mileageDesignerVoList = mileageMapper.selectMileageDepositList(mileageDto);
		if(mileageDesignerVoList != null && mileageDesignerVoList.size() > 0){
			for (int i = 0; i < mileageDesignerVoList.size(); i++) {
				MileageDesignerVo mileageDesignerVo = mileageDesignerVoList.get(i);
				String payRefundStatus = mileageDesignerVo.getPayRefundStatus(); // 전체환불, 일부환불 상태 확인
				int refundAmount = mileageDesignerVo.getPaymentAmount();    // 일부환불 금액
				mileageDesignerVo.setExpectedAmount(CalculateUtil.calculateRefundAmount(mileageDesignerVo.getMileageUnit(), mileageDesignerVo.getMileageAmount(), exchangeAmount, payRefundStatus, refundAmount));
			}
		}
		mileageDesignerListVo.setList(mileageDesignerVoList);
		mileageDesignerListVo.setCnt(mileageMapper.selectMileageDepositListCnt(mileageDto));

		return mileageDesignerListVo;
	}

	public MileageDesignerCalculateListVo getMileageCalculate(MileageDto mileageDto) {
		mileageDto.setRegisterNo(SecurityUtil.getMemberNo());

		MileageDesignerCalculateListVo mileageDesignerCalculateListVo = new MileageDesignerCalculateListVo();
		mileageDesignerCalculateListVo.setList(mileageMapper.selectMileageCalculateList(mileageDto));
		mileageDesignerCalculateListVo.setCnt(mileageMapper.selectMileageCalculateListCnt(mileageDto));

		return mileageDesignerCalculateListVo;
	}

	public HashMap<String, Object> getEasyPay(Integer amount) {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		String uuid = UUID.randomUUID().toString();

		int result = mileageMapper.insertEasyPay(amount, uuid, SecurityUtil.getMemberNo());

		if(result > 0){
			resultMap.put("result", "success");
			resultMap.put("moid", uuid);
		}else{
			resultMap.put("result", "fail");
		}

		return resultMap;
	}

	public int postEasy(String str, String unit) throws Exception{
		int result = 0;

		String encryptedString = URLDecoder.decode(str, "UTF-8");
		if (encryptedString.startsWith("\"") && encryptedString.endsWith("\"")) {
			encryptedString = encryptedString.substring(1, encryptedString.length() - 1);
		}

		String decryptedString = AesCryptUtil.decrypt(encryptedString);

		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, Object> map = objectMapper.readValue(decryptedString, new TypeReference<Map<String, Object>>() {});
		System.out.println("map = " + map.toString());

		if (map.containsKey("Currency")) {
			if (map.containsKey("Amt")) {
				int amtValue = Integer.parseInt(map.get("Amt").toString());
				map.put("Amt", amtValue / 100);
			}
		}
		// 1. 결제 완료 처리가 되어있는지 확인.
		int cnt = mileageMapper.selectEasyPayment(map);
		if(cnt > 0){
			throw new Exception("이미 완료 처리된 주문번호 입니다.");
		}

		// 2. 데이터 검증
		int cnt2 = mileageMapper.selectEasyPaymentVerification(map);
		if(cnt2 == 0){
			throw new Exception("금액이 일치하지 않습니다. 데이터 위변조를 확인해주시기 바랍니다.");
		}

		// 3. 결제 정보 테이블에 데이터 update
		map.put("jsonData", map.toString());
		int resultCnt = mileageMapper.updateEasyPayment(map);

		// 4. 마일리지 충전
		if(resultCnt > 0){
			if("3001".equals(map.get("ResultCode").toString())){
				MileageAddDto mileageAddDto = new MileageAddDto();
				mileageAddDto.setMileageSe("A");
				mileageAddDto.setMileageUnit(unit);
				mileageAddDto.setMileageCn(map.get("GoodsName").toString());
				mileageAddDto.setMileageAmount(Integer.parseInt(map.get("Amt").toString()));	// 금액
				mileageAddDto.setRegisterNo(SecurityUtil.getMemberNo());
				mileageAddDto.setOrderNumber(map.get("MOID").toString());
				result = mileageMapper.insertMileageCharge(mileageAddDto);
				
				int isAlarm = commonService.selectAlarm(SecurityUtil.getMemberNo(), 5);
	            if (isAlarm > 0) {

					// 2025-03-05 cjj 로그인한 사람이 국내/해외 의뢰인인지 확인.
					// 회원유형 (A:한국인, B:외국인)
					String memberTp = commonService.selectMemberTp(SecurityUtil.getMemberNo());
					String msg = "";
					if("A".equals(memberTp)){
						msg = "마일리지충전";
					}else{
						msg = "Charge mileage";
					}

	                PushDto push = new PushDto();
	                push.setBody(msg + " " + map.get("Amt").toString());
	                commonService.postFCMPush(SecurityUtil.getMemberNo(), push, "/mileage");
	                
	                AlarmAddDto alarmAddDto = new AlarmAddDto();
	                alarmAddDto.setAlarmSj(msg);
	                alarmAddDto.setAlarmCn(msg + " " + map.get("Amt").toString());
	                alarmAddDto.setAlarmSe("F");
	                alarmAddDto.setAlarmUrl("");
	                alarmAddDto.setMemberNo(SecurityUtil.getMemberNo());
	                commonService.postAlarm(alarmAddDto);
	            }
				
			}
		}

		return result;
	}

	public int postPaypal(String str) throws Exception{
		System.out.println("Str = " + str);
		int result = 0;

		String encryptedString = URLDecoder.decode(str, "UTF-8");
		if (encryptedString.startsWith("\"") && encryptedString.endsWith("\"")) {
			encryptedString = encryptedString.substring(1, encryptedString.length() - 1);
		}

		String decryptedString = AesCryptUtil.decrypt(encryptedString);

		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, Object> map = objectMapper.readValue(decryptedString, new TypeReference<Map<String, Object>>() {});
		System.out.println("map = " + map.toString());
		// 1. 결제 완료 처리가 되어있는지 확인.
		int cnt = mileageMapper.selectPaypal(map);
		if(cnt > 0){
			throw new Exception("이미 완료 처리된 주문번호 입니다.");
		}

		// 2. 데이터 검증
		int cnt2 = mileageMapper.selectPaypalPaymentVerification(map);
		if(cnt2 == 0){
			throw new Exception("금액이 일치하지 않습니다. 데이터 위변조를 확인해주시기 바랍니다.");
		}

		// 3. 마일리지 충전
		MileageAddDto mileageAddDto = new MileageAddDto();
		mileageAddDto.setMileageSe("A");
		mileageAddDto.setMileageUnit("B");
		mileageAddDto.setMileageCn(map.get("GoodsName").toString());
		mileageAddDto.setMileageAmount(Integer.parseInt(map.get("Amt").toString()));	// 금액
		mileageAddDto.setRegisterNo(SecurityUtil.getMemberNo());
		mileageAddDto.setOrderNumber(map.get("moId").toString());
		result = mileageMapper.insertMileageCharge(mileageAddDto);
		
		int isAlarm = commonService.selectAlarm(SecurityUtil.getMemberNo(), 5);
        if (isAlarm > 0) {
            PushDto push = new PushDto();
            push.setBody("Mileage charging " + map.get("Amt").toString());
            commonService.postFCMPush(SecurityUtil.getMemberNo(), push, "/mileage");
            
            AlarmAddDto alarmAddDto = new AlarmAddDto();
            alarmAddDto.setAlarmSj("Mileage charging");
            alarmAddDto.setAlarmCn(map.get("Amt").toString() + " mileage recharge");
            alarmAddDto.setAlarmSe("F");
            alarmAddDto.setAlarmUrl("");
            alarmAddDto.setMemberNo(SecurityUtil.getMemberNo());
            commonService.postAlarm(alarmAddDto);
        }

		// 4. 결제 정보 테이블에 데이터 update
		map.put("mileageNo", mileageAddDto.getMileageNo());
		int resultCnt = mileageMapper.updatePaypal(map);


		return resultCnt;
	}

	public HashMap<String, Object> getPaypal(Integer amount) {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		String uuid = UUID.randomUUID().toString();

		int result = mileageMapper.insertPaypal(amount, uuid, SecurityUtil.getMemberNo());

		if(result > 0){
			resultMap.put("result", "success");
			resultMap.put("moid", uuid);
		}else{
			resultMap.put("result", "fail");
		}

		return resultMap;
	}
}

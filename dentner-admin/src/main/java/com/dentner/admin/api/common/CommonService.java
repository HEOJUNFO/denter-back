package com.dentner.admin.api.common;

import com.dentner.core.cmmn.dto.*;
import com.dentner.core.cmmn.mapper.CommonMapper;
import com.dentner.core.cmmn.service.FileUploadService;
import com.dentner.core.cmmn.service.MailService;
import com.dentner.core.cmmn.vo.CodeVo;
import com.dentner.core.cmmn.vo.FileVO;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.popbill.api.KakaoService;
import com.popbill.api.PopbillException;
import com.popbill.api.kakao.KakaoButton;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommonService {

	@Autowired
	CommonMapper commonMapper;

	@Autowired
	private MailService mailService;

	@Autowired
	private FileUploadService fileUploadService;

	@Value("${popbill.corpNum}")
	private String CorpNum;

	@Value("${popbill.senderNum}")
	private String senderNum;

	@Value("${popbill.userID}")
	private String userID;

	@Autowired
	private KakaoService kakaoService;

	public MailDto postMail(MailDto mailDto) {
		try {
			mailService.mailSend(mailDto);
		}catch (Exception e){
		}
		return mailDto;
	}

	@Transactional
	public FileVO postEditFile(FileDto fileDto) throws IOException {
		FileVO fileVO = fileUploadService.uploadToBase64(fileDto.getFileName(), fileDto.getFileBase64(), "editor");
		return fileVO;
    }

	public List<CodeVo> getCodeList(Integer parentNo, String type) {
		return commonMapper.selectCodeList(parentNo, type);
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

	public int postAlarm(AlarmAddDto alarmAddDto) {
		return commonMapper.insertAlarm(alarmAddDto);
	}

	public int postAllAlarm(AlarmAddDto alarmAddDto) {
		return commonMapper.insertAllAlarm(alarmAddDto);
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
	
	public int postFCMPush(int memberNo, PushDto pushDto, String goUrl) {
		try {
			List<String> token = commonMapper.selectMemberFCMToken(memberNo);
			if(token != null && token.size() > 0){
				for (int i = 0; i < token.size(); i++) {
					Message message = Message.builder()
							.putData("title", "Dentner")
							.putData("body", pushDto.getBody())
							.putData("url", goUrl)
							.setToken(token.get(i))
							.build();

					String response = FirebaseMessaging.getInstance().send(message);
					System.out.println("Successfully sent message: " + response);

				}
			}
			return 1;

		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public String selectMemberTp(int memberNo) {
		return commonMapper.selectMemberTp(memberNo);
	}

	public String getMemberNickName(String memberNo) {
		return commonMapper.selectMemberNickName(memberNo);
	}
}

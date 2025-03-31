package com.dentner.front.api.chat;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dentner.core.cmmn.dto.AlarmAddDto;
import com.dentner.core.cmmn.dto.ChatAddDto;
import com.dentner.core.cmmn.dto.ChatDto;
import com.dentner.core.cmmn.dto.ChatRoomAddDto;
import com.dentner.core.cmmn.dto.ChatRoomDto;
import com.dentner.core.cmmn.dto.PushDto;
import com.dentner.core.cmmn.mapper.ChatMapper;
import com.dentner.core.cmmn.vo.ChatListVo;
import com.dentner.core.cmmn.vo.ChatRoomListVo;
import com.dentner.core.cmmn.vo.ChatRoomVo;
import com.dentner.core.util.SecurityUtil;
import com.dentner.front.api.common.CommonService;

import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {

	@Autowired
	ChatMapper chatMapper;

	@Resource(name= "commonService")
	CommonService commonService;

	public Integer postChatRoom(ChatRoomAddDto chatRoomAddDto) {
		// 1. 토큰에서 회원 타입을 꺼낸다.
		String memberSe = SecurityUtil.getMemberSe();

		if("A".equals(memberSe)){	// 의뢰인 일때
			chatRoomAddDto.setMemberNo(SecurityUtil.getMemberNo());
		}else{	// 치과기공소 or 치자이너
			chatRoomAddDto.setMemberNo(chatRoomAddDto.getTargetNo());
			chatRoomAddDto.setTargetNo(SecurityUtil.getMemberNo());
		}
		// 기존 방이 있는지 확인.
		Integer isExist = chatMapper.selectRoomExist(chatRoomAddDto);
		if(isExist != null){
			return isExist;
		}
		int result = chatMapper.insertChatRoom(chatRoomAddDto);

		return chatRoomAddDto.getRoomNo();

	}

	public ChatRoomListVo getChatRoomList(String targetSe, String memberSe, ChatRoomDto chatRoomDto) {
		// 1. 토큰에서 회원 타입을 꺼낸다.
		//String memberSe = SecurityUtil.getMemberSe();

		List<ChatRoomVo> roomList = new ArrayList<ChatRoomVo>();
		int cnt = 0;
		chatRoomDto.setMemberNo(SecurityUtil.getMemberNo());
		if("A".equals(memberSe)){	// 의뢰인 일때
			chatRoomDto.setTargetSe(targetSe);

			roomList = chatMapper.selectChatRoomRequestList(chatRoomDto);
			cnt = chatMapper.selectChatRoomRequestListCnt(chatRoomDto);
		}else{	// 치과기공소 or 치자이너
			chatRoomDto.setTargetSe(memberSe);

			roomList = chatMapper.selectChatRoomList(chatRoomDto);
			cnt = chatMapper.selectChatRoomListCnt(chatRoomDto);
		}

		ChatRoomListVo chatRoomListVo = new ChatRoomListVo();
		chatRoomListVo.setList(roomList);
		chatRoomListVo.setCnt(cnt);

		return chatRoomListVo;
	}

	public int deleteChatRoom(String roomNoArr) {
		String memberSe = SecurityUtil.getMemberSe();
		int memberNo = SecurityUtil.getMemberNo();

		return chatMapper.deleteChatRoom(roomNoArr, memberNo, memberSe);
	}

	public ChatListVo getChatRoomDetail(Integer roomNo, ChatDto chatDto) {
		ChatListVo chatListVo = new ChatListVo();

		chatDto.setRoomNo(roomNo);
		chatDto.setToNo(SecurityUtil.getMemberNo());
		// 1. 토큰에서 회원 타입을 꺼낸다.
		String memberSe = SecurityUtil.getMemberSe();

		// 받은 메세지 모두 읽음처리
		chatMapper.updateChatReadYn(chatDto);

		if("A".equals(memberSe)){	// 의뢰인 일때
			chatListVo.setList(chatMapper.selectChatRoomRequestDetail(chatDto));
			chatListVo.setCnt(chatMapper.selectChatRoomRequestDetailCnt(chatDto));
		}else{	// 치과기공소 or 치자이너
			chatListVo.setList(chatMapper.selectChatRoomDetail(chatDto));
			chatListVo.setCnt(chatMapper.selectChatRoomDetailCnt(chatDto));
		}

		return chatListVo;
	}

	public int postChat(Integer roomNo, ChatAddDto chatAddDto) {
		int result = 0;
		chatAddDto.setRoomNo(roomNo);
		//chatAddDto.setFromNo(SecurityUtil.getMemberNo());
		//chatAddDto.setMemberSe(SecurityUtil.getMemberSe());

		String msgType = chatAddDto.getMsgType();
		//if("1".equals(msgType)){
		result = chatMapper.insertChat(chatAddDto);
		//}

		String msg = chatAddDto.getMsg();
		if("2".equals(msgType) && "".equals(msg)){
			msg = "[파일]";
		} else if ("3".equals(msgType) && "".equals(msg)) {
			msg = "[이미지]";
		}
		
		ArrayList<String> list = new ArrayList<String>();
		list.add("6");
		list.add("12");
		
		int isAlarm = commonService.selectAlarm(chatAddDto.getToNo(), list);
        if (isAlarm > 0) {
			// 2025-03-05 cjj 로그인한 사람이 국내/해외 의뢰인인지 확인.
			// 회원유형 (A:한국인, B:외국인)
			String memberTp = commonService.selectMemberTp(chatAddDto.getToNo());
			String sj = "";
			if("A".equals(memberTp)){
				sj = "채팅이 도착하였습니다.";
			}else{
				sj = "You received a Chat";
			}
        	// 알림 추가
        	AlarmAddDto alarmAddDto = new AlarmAddDto();
        	alarmAddDto.setAlarmSj(sj);
        	alarmAddDto.setAlarmCn(msg);
        	alarmAddDto.setAlarmSe("A");
        	alarmAddDto.setAlarmUrl(roomNo.toString());
        	alarmAddDto.setMemberNo(chatAddDto.getToNo());
        	commonService.postAlarm(alarmAddDto);
        	
        	PushDto push = new PushDto();
            push.setBody(msg);
            commonService.postFCMPush(chatAddDto.getToNo(), push, "/chat");
        }
		

		return chatAddDto.getChatNo();
	}

    public int getChatUnreadCnt(Integer memberNo) {
		return chatMapper.selectChatUnreadCnt(memberNo);
    }

	public Integer getRoomParticipants(Integer chatNo) {
		return chatMapper.selectRoomParticipants(chatNo);
	}
}

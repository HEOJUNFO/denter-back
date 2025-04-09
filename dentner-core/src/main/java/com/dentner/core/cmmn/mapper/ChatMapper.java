package com.dentner.core.cmmn.mapper;

import com.dentner.core.cmmn.dto.ChatAddDto;
import com.dentner.core.cmmn.dto.ChatDto;
import com.dentner.core.cmmn.dto.ChatRoomAddDto;
import com.dentner.core.cmmn.dto.ChatRoomDto;
import com.dentner.core.cmmn.vo.ChatRoomVo;
import com.dentner.core.cmmn.vo.ChatVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatMapper {

    int insertChatRoom(ChatRoomAddDto chatRoomAddDto);

    List<ChatRoomVo> selectChatRoomRequestList(ChatRoomDto chatRoomDto);

    int selectChatRoomRequestListCnt(ChatRoomDto chatRoomDto);

    List<ChatRoomVo> selectChatRoomList(ChatRoomDto chatRoomDto);

    int selectChatRoomListCnt(ChatRoomDto chatRoomDto);

    int deleteChatRoom(@Param("roomNoArr") String roomNoArr, @Param("memberNo")int memberNo, @Param("memberSe")String memberSe);

    List<ChatVo> selectChatRoomDetail(ChatDto chatDto);

    int selectChatRoomRequestDetailCnt(ChatDto chatDto);

    List<ChatVo> selectChatRoomRequestDetail(ChatDto chatDto);

    int selectChatRoomDetailCnt(ChatDto chatDto);

    int insertChat(ChatAddDto chatAddDto);

    int selectChatUnreadCnt(@Param("memberNo") Integer memberNo);

    Integer selectRoomParticipants(@Param("chatNo")Integer chatNo);

    Integer selectRoomExist(ChatRoomAddDto chatRoomAddDto);

    void updateChatReadYn(ChatDto chatDto);

    ChatRoomVo selectChatRoomRequestForRequest(ChatRoomDto chatRoomDto);

}

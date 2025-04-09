package com.dentner.front.api.chat;


import com.dentner.core.cmmn.dto.ChatAddDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Tag(name = "채팅 socket api", description = "채팅 소켓 API")
public class ChatSocketController{

	@Resource(name= "chatService")
    ChatService chatService;

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/login/{memberNo}")
    public void handleLogin(@DestinationVariable Integer memberNo) {
        int unreadCnt = chatService.getChatUnreadCnt(memberNo);

        try {
            messagingTemplate.convertAndSend("/topic/updates/" + memberNo, unreadCnt);
            System.out.println("Message sent successfully to /topic/updates/" + memberNo);
        } catch (Exception e) {
            System.err.println("Failed to send message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @MessageMapping("/chat/{roomNo}")
    @SendTo("/topic/chat/{roomNo}")
    public ChatAddDto receiveMessage(Message<ChatAddDto> message, @DestinationVariable Integer roomNo) {
        // 받은 메시지 처리 로직
        if(!"0".equals(message.getPayload().getMsgType())){
            // 2. 메시지 데이터베이스 저장
            int chatNo = chatService.postChat(roomNo, message.getPayload());
            // 채팅 메세지를 받는 참여자에게 업데이트 알림
            Integer memberNo = chatService.getRoomParticipants(chatNo);
            messagingTemplate.convertAndSend("/topic/updates/" + memberNo, "new_message");
        }
        // 메시지를 처리한 후 반환하면 해당 주제(topic)로 다시 전송됨
        return message.getPayload();
    }
}

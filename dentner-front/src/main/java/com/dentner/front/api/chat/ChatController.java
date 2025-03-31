package com.dentner.front.api.chat;


import com.dentner.core.cmmn.dto.ChatAddDto;
import com.dentner.core.cmmn.dto.ChatDto;
import com.dentner.core.cmmn.dto.ChatRoomAddDto;
import com.dentner.core.cmmn.dto.ChatRoomDto;
import com.dentner.core.cmmn.service.response.ResponseMessage;
import com.dentner.core.cmmn.vo.ChatListVo;
import com.dentner.core.cmmn.vo.ChatRoomListVo;
import com.dentner.core.util.SecurityUtil;
import com.dentner.front.version.V1ApiVersion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "채팅 api", description = "채팅 API")
public class ChatController implements V1ApiVersion {

	@Resource(name= "chatService")
    ChatService chatService;

    @PostMapping("/chat/room")
    @ResponseMessage("채팅 방 생성 성공")
    @Operation(summary = "채팅 방 생성", description = "채팅 방을 생성한다.")
    @Parameters({
            @Parameter(name = "targetNo", description = "채팅 상대방 번호", example = "",  schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "memberSe", description = "회원 구분(A:의뢰인,B:치과기공소,C:치자이너)", example = "",  schema = @Schema(type = "string"))
    })
    public Integer postChatRoom(@RequestBody ChatRoomAddDto chatRoomAddDto){
        return chatService.postChatRoom(chatRoomAddDto);
    }

    @GetMapping("/chat/room/{targetSe}/{memberSe}")
    @ResponseMessage("채팅 방 목록 조회 성공")
    @Operation(summary = "채팅 방 목록", description = "채팅 방 목록을 조회한다.")
    @Parameters({
            @Parameter(name = "targetSe", description = "타겟 구분(A:의뢰인,B:치과기공소,C:치자이너)", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "memberSe", description = "회원 구분(A:의뢰인,B:치과기공소,C:치자이너)", example = "",  schema = @Schema(type = "string"))
    })
    public ChatRoomListVo getChatRoomList(@PathVariable(required = false) String targetSe,
                                          @PathVariable(required = false) String memberSe, @ModelAttribute ChatRoomDto chatRoomDto) {
        return chatService.getChatRoomList(targetSe, memberSe, chatRoomDto);
    }

    @DeleteMapping("/chat/room/{roomNoArr}")
    @ResponseMessage("채팅방 나가기 성공")
    @Operation(summary = "채팅방 나가기", description = "채팅방을 나가기한다.")
    @Parameter(name = "roomNoArr", description = "채팅방 번호(콤마로 구분)", example = "",  schema = @Schema(type = "string"))
    public int deleteChatRoom(@PathVariable(required = false) String roomNoArr){
        return chatService.deleteChatRoom(roomNoArr);
    }

    @GetMapping("/chat/room/detail/{roomNo}")
    @ResponseMessage("채팅 방 상세 조회 성공")
    @Operation(summary = "채팅 방 상세", description = "채팅 방 상세를 조회한다.")
    @Parameters({
            @Parameter(name = "startRow", description = "페이징 시작번호", example = "",  schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "pageCnt", description = "페이징 카운트", example = "",  schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "targetNo", description = "채팅 상대방 번호", example = "",  schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "memberSe", description = "회원 구분(A:의뢰인,B:치과기공소,C:치자이너)", example = "",  schema = @Schema(type = "string"))
    })
    public ChatListVo getChatRoomDetail(@PathVariable(required = false) Integer roomNo ,@ModelAttribute ChatDto chatDto) {
        return chatService.getChatRoomDetail(roomNo, chatDto);
    }

    @PostMapping("/chat/{roomNo}")
    @ResponseMessage("채팅 보내기 성공")
    @Operation(summary = "채팅 보내기", description = "채팅을 보낸다.")
    @Parameters({
            @Parameter(name = "roomNo", description = "채팅 룸 번호", example = "",  schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "msg", description = "메세지", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "msgType", description = "메세지 타입", example = "" ,  schema = @Schema(type = "string"))
    })
    public int postChat(@PathVariable(required = false) Integer roomNo, @ModelAttribute ChatAddDto chatAddDto){
        return chatService.postChat(roomNo, chatAddDto);
    }

    @GetMapping("/chat/read-cnt")
    @ResponseMessage("채팅 읽지 않은 수 조회 성공")
    @Operation(summary = "채팅 읽지 않은 수 조회", description = "채팅 읽지 않은 수를 조회한다.")
    public int getChatReadCnt() {
        return chatService.getChatUnreadCnt(SecurityUtil.getMemberNo());
    }

}

package v1.erpback.chat.controlller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;
import v1.erpback.chat.dto.MessageDTO;
import v1.erpback.chat.service.ChatService;
import v1.erpback.noti.service.NotiService;
import v1.erpback.room.domain.Room;
import v1.erpback.room.service.RoomService;
import v1.erpback.user.domain.User;
import v1.erpback.user.repository.UserMapper;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
@RestController
public class ChatController {
    private final SimpMessagingTemplate template;
    private final ChatService chatService; // 채팅 메시지 저장용 서비스 (추가)
    private final UserMapper userMapper;
    private final NotiService notiService;
    private final RoomService roomService;

    @MessageMapping("/room/{roomId}/entered")
    public void entered(@DestinationVariable(value = "roomId") Long roomId, MessageDTO message) {
        log.info("# roomId = {}", roomId);
        log.info("# message = {}", message.getWriter());


        message.setType("ENTER");
        String name = userMapper.findByAccount(message.getWriter()).getName();
        message.setWriter(name);
        message.setMessage(message.getWriter() + "님이 입장하셨습니다.");
        // 메시지를 클라이언트로 전송
        template.convertAndSend("/sub/room/" + roomId, message);
    }

    // 채팅 메시지 처리
    @MessageMapping("/room/{roomId}")
    public void sendMessage(@DestinationVariable(value = "roomId") Long roomId, MessageDTO message) {
        try {
            if (message == null || message.getMessage() == null) {
                throw new IllegalArgumentException("Invalid message payload");
            }

            log.info("# roomId = {}", roomId);
            log.info("# message = {}", message.getMessage());
            User user = userMapper.findByAccount(message.getWriter());
            String name = user.getName();
            Room room = roomService.findById(roomId);
            List<Long> chatUsers = roomService.findRoomUsers(roomId);
            // DB에 메시지 저장
            chatService.saveMessage(roomId, message);
            for (Long id : chatUsers) {
                if (!Objects.equals(user.getId(), id)) {
                    notiService.save(id, room.getName() + "방에서" + message.getWriter() + "님이 메시지를 보냈습니다.");
                }
            }
            message.setWriter(name);
            // 메시지를 클라이언트로 전송
            template.convertAndSend("/sub/room/" + roomId, message);
        } catch (Exception e) {
            log.error("Error sending message: ", e);
        }
    }

    // 사용자 퇴장 처리
    @MessageMapping("/room/{roomId}/left")
    public void left(@DestinationVariable(value = "roomId") Long roomId, MessageDTO message) {
        log.info("# roomId = {}", roomId);
        log.info("# message = {}", message.getWriter());

        message.setType("LEAVE");
        String name = userMapper.findByAccount(message.getWriter()).getName();

        message.setWriter(name);
        message.setMessage(message.getWriter() + "님이 퇴장하셨습니다.");

        // 메시지를 클라이언트로 전송
        template.convertAndSend("/sub/room/" + roomId, message);
    }
}

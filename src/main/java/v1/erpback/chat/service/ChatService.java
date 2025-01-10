package v1.erpback.chat.service;

import lombok.RequiredArgsConstructor;
import org.apache.catalina.util.CharsetMapper;
import org.springframework.stereotype.Service;
import v1.erpback.chat.domain.Message;
import v1.erpback.chat.dto.MessageDTO;
import v1.erpback.chat.dto.MessageSaveDTO;
import v1.erpback.chat.repository.MessageMapper;
import v1.erpback.user.repository.UserMapper;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final MessageMapper messageMapper;
    private final UserMapper userMapper;

    public void saveMessage(Long roomId, MessageDTO message) {
        MessageSaveDTO messageSaveDTO = MessageSaveDTO.builder()
                .roomId(roomId)
                .senderId(userMapper.findByAccount(message.getWriter()).getId())
                .content(message.getMessage())
                .build();

        messageMapper.save(messageSaveDTO);
    }
}

package v1.erpback.chat.repository;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import v1.erpback.chat.domain.Message;
import v1.erpback.chat.dto.MessageSaveDTO;

@Mapper
public interface MessageMapper {

    @Insert("INSERT INTO chat_message (room_id, sender_id, content) VALUES (#{roomId}, #{senderId}, #{content})")
    void save(MessageSaveDTO message);
}

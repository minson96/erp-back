package v1.erpback.postMessage.repository;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import v1.erpback.postMessage.dto.PostMessageReceiver;

@Mapper
public interface PostMessageReceiverMapper {
    @Insert("INSERT INTO post_message_receiver (post_message_id, receiver_id, receiver_data, status) VALUES (#{postMessageId}, #{receiverId}, #{receiverData}, #{status})")
    void create(PostMessageReceiver receiver);
}

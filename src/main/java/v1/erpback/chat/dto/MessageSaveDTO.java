package v1.erpback.chat.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class MessageSaveDTO {
    private Long roomId;
    private Long senderId;
    private String content;
}

package v1.erpback.postMessage.dto;

import lombok.*;
import v1.erpback.postMessage.domain.PostMessage;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostMessageReceiver {
    private Long id;
    private Long postMessageId;
    private Long receiverId;
    private String receiverData;
    private MessageStatus status;

    public enum MessageStatus {
        ALIVE,
        TRASH,
        DELETE
    }
}

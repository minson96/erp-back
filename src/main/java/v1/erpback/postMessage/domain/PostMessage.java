package v1.erpback.postMessage.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostMessage {
    private Long id;

    private Long userId;

    private String title;

    private String content;

    private LocalDateTime createdAt;

    private MessageStatus status;

    private LocalDateTime deletedAt;

    private Boolean fileAttached;

    public enum MessageStatus {
        ALIVE,
        TRASH,
        DELETE
    }
}

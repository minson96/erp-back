package v1.erpback.room.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatUser {
    private Long id;
    private Long roomId;
    private Long userId;
    private LocalDateTime joinedAt;
}

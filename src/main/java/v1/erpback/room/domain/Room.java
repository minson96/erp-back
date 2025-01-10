package v1.erpback.room.domain;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
@Builder
public class Room {
    private Long id;
    private String name;
    private boolean isGroup;
    private LocalDateTime createdAt;
}

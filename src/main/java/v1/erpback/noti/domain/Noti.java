package v1.erpback.noti.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Noti {
    private Long id;
    private Long userId;
    private String message;
    private LocalDateTime createdAt;
    private Boolean readAt;
}

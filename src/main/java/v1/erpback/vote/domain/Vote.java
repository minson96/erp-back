package v1.erpback.vote.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vote {
    private Long id;
    private Long userId;
    private String title;
    private Status status;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    public enum Status {
        PENDING,
        END
    }
}

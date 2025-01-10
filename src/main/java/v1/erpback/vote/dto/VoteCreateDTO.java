package v1.erpback.vote.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VoteCreateDTO {
    private String title;
    private List<String> choices;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private List<Long> userId;
    private List<Long> departmentId;
    private Long writerId;
}

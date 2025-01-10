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
public class VoteDetailDTO {
    private Long id;
    private Long userId;
    private String userName;
    private String status;
    private String title;
    private String userSelect;
    private String userNames;
    private String departmentNames;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String answers;
    private String answersCount;
}

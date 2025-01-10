package v1.erpback.vote.domain;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteUser {
    private Long id;
    private Long userId;
    private Long voteId;
    private Long departmentId;
    private Long voteResultId;
}

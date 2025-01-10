package v1.erpback.vote.domain;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteResult {
    private Long id;
    private Long voteId;
    private String answer;
    private Long answerCount;
}

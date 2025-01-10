package v1.erpback.board.domain;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Board {
    private Long id;
    private String name;
    private Long companyId;
}

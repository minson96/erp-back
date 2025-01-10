package v1.erpback.post.domain;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Post {
    private Long id;
    private Long boardId;
    private Long userId;
    private String title;
    private String content;
    private boolean fileAttached;
    private LocalDateTime createdAt;
}

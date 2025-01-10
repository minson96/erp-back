package v1.erpback.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostListDTO {
    private Long id;
    private String title;
    private String boardName;
    private LocalDateTime createdAt;
}

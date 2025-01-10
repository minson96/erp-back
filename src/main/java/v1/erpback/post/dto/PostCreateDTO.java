package v1.erpback.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateDTO {
    private Long boardId;
    private Long userId;
    private String title;
    private String content;
    private List<MultipartFile> files;
}

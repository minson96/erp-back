package v1.erpback.postMessage.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostMessageSendListDTO {

    private Long id;

    private String receivers;

    private String title;

    private String content;

    private String createdAt;

    private String files;
}

package v1.erpback.postMessage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostMessageSendDTO {

    private Long userId;

    private String receiverUserIds;

    private String receiverDepartmentIds;

    private String title;

    private String content;

    private List<MultipartFile> files;
}

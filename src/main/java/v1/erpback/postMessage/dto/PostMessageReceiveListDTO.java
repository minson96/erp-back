package v1.erpback.postMessage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostMessageReceiveListDTO {
    private Long id;
    private String title;
    private String content;
    private String createdAt;
    private String deletedAt;
    private Long userId;
    private String files;
    private String sender;

}

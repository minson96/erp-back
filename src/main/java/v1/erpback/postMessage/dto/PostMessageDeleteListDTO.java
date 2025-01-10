package v1.erpback.postMessage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostMessageDeleteListDTO {
    private Long id;
    private String title;
    private String content;
    private String createdAt;
    private String deletedAt;
    private Long userId;
    private String files;
    private String receivers;
    private String sender;
    private String messageType;

}

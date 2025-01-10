package v1.erpback.room.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageLoadDTO {
    private Long id;
    private Long roomId;
    private String userName;
    private String message;
    private String createdAt;
}

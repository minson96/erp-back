package v1.erpback.room.dto;

import lombok.Data;

@Data
public class RoomListDTO {
    private Long id;
    private String name;
    private boolean isGroup;
    private String createdAt;
}

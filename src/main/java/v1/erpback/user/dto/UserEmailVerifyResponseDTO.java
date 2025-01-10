package v1.erpback.user.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserEmailVerifyResponseDTO {
    private Boolean verified;
    private String message;
}

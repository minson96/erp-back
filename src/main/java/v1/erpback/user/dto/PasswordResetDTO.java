package v1.erpback.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetDTO {
    private String email;
    private String name;
    private String newPassword;
}

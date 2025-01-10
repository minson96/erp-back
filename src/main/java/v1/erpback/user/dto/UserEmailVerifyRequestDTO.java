package v1.erpback.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserEmailVerifyRequestDTO {
    private String email;
    private String verificationCode;
}

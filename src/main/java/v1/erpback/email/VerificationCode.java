package v1.erpback.email;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Optional;

@Builder
@Getter
@Setter
public class VerificationCode {
    private String email;
    private String codes;
    private LocalDateTime expiresTime;

}

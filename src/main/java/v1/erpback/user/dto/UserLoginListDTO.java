package v1.erpback.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginListDTO {
    private Long id;
    private String ip;
    private String userAgent;
    private String os;
    private LocalDateTime loginTime;
}

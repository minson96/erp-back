package v1.erpback.auth.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Builder
@Getter
public class Jwt {
    private Long loginId;
    private String accessToken;
    private String refreshToken;
    private Date accessTokenExp;
    private Date refreshTokenExp;

}

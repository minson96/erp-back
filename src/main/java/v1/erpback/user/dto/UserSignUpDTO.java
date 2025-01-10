package v1.erpback.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSignUpDTO {

    private Long companyId;

    private String name;

    private String account;

    private String password;

    private String email;

    private Boolean isVerified;

    private Long positionId;

    private Long departmentId;

    private MultipartFile profileImg;
}

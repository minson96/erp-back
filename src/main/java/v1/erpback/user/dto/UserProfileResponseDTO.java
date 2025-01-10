package v1.erpback.user.dto;

import lombok.*;
import v1.erpback.user.domain.User;

import java.net.URLEncoder;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponseDTO {
    private Long id;
    private String name;
    private String account;
    private String email;
    //department
    private Long departmentId;
    private String departmentName;
    private String positionName;
    private Long positionId;
    private User.SignUpStatus status;
    private String role;
    private String phoneNumber;
    private String companyNumber;
    private String storedFileName;
    private Long companyId;
}

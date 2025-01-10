package v1.erpback.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import v1.erpback.user.domain.User;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminsUserListDTO {
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
    private String phoneNumber;
    private String companyNumber;
    private String profileImg;
}

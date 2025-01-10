package v1.erpback.user.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    public enum Role {
        USER, ADMIN, SUPER_ADMIN, HOD
    }
    public enum SignUpStatus {
        PENDING_APPROVAL,
        APPROVED,
        WITHDRAWAL
    }
    private Long id;
    private Long companyId;
    private String name;
    private String account;
    private String password;
    private String email;
    //department
    private Long departmentId;
    private String companyNumber;
    private String phoneNumber;
    private Long positionId;
    private SignUpStatus status;
    private Role role;
    private LocalDateTime createdAt;
    //file
    private String originalFileName;
    private String storedFileName;
}

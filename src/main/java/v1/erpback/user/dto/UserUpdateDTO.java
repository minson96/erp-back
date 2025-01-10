package v1.erpback.user.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserUpdateDTO {
    private Long id;

    private String account;
    private String email;
    private String phoneNumber;
    private String companyNumber;

    private Optional<Long> positionId = Optional.empty();;

    private Optional<Long> departmentId = Optional.empty();;

    private MultipartFile profileImg;
}

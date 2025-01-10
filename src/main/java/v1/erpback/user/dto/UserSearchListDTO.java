package v1.erpback.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchListDTO {
    private Long id;
    private String name;
    private String account;
    private String type;
    private String role;
}

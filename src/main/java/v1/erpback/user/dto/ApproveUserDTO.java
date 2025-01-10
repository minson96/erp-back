package v1.erpback.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApproveUserDTO {
    private List<Long> ids;
}

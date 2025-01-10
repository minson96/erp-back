package v1.erpback.department.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepartmentCreateDTO {
    private String name;
    private Long parentId;
    private Long companyId;
}

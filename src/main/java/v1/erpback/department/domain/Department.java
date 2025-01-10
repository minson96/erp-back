package v1.erpback.department.domain;

import lombok.*;

import v1.erpback.department.dto.DepartmentListResponseDTO;

import java.util.ArrayList;
import java.util.List;


@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Setter
@Data
public class Department {
    private Long id;
    private String name;
    private Long companyId;
    private Long parentId;


}

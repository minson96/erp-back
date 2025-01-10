package v1.erpback.department.dto;

import lombok.*;
import v1.erpback.department.domain.Department;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DepartmentListResponseDTO {
    private Long id;
    private String name;
    private Long parentId;
    private Long companyId;
    private List<DepartmentListResponseDTO> subDepartments;

//    public static DepartmentListResponseDTO fromEntity(Department department) {
//        return DepartmentListResponseDTO.builder()
//                .id(department.getId())
//                .name(department.getName())
//                .parentId(department.getParentId())
//                .companyId(department.getCompanyId())
//                .build();
//    }
//
//    public static Builder builder() {
//        return new Builder();
//    }
//
//    public static class Builder {
//        private Long id;
//        private String name;
//        private Long parentId;
//        private Long companyId;
//
//        public Builder id(Long id) {
//            this.id = id;
//            return this;
//        }
//
//        public Builder name(String name) {
//            this.name = name;
//            return this;
//        }
//
//        public Builder parentId(Long parentId) {
//            this.parentId = parentId;
//            return this;
//        }
//
//        public Builder companyId(Long companyId) {
//            this.companyId = companyId;
//            return this;
//        }
//
//        public DepartmentListResponseDTO build() {
//            DepartmentListResponseDTO dto = new DepartmentListResponseDTO();
//            dto.id = this.id;
//            dto.name = this.name;
//            dto.parentId = this.parentId;
//            dto.companyId = this.companyId;
//            return dto;
//        }
//    }
}

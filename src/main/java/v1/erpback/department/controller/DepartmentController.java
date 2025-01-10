package v1.erpback.department.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import v1.erpback.Exception.CustomException;
import v1.erpback.department.dto.DepartmentCreateDTO;
import v1.erpback.department.dto.DepartmentListRequestDTO;
import v1.erpback.department.dto.DepartmentListResponseDTO;
import v1.erpback.department.dto.DepartmentSearchListDTO;
import v1.erpback.department.service.DepartmentService;
import v1.erpback.user.domain.User;
import v1.erpback.user.dto.UserSearchListDTO;
import v1.erpback.util.PageResultDTO;

import java.util.List;

@RestController
@RequestMapping("/api/v1/department")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentService departmentService;

    @PostMapping("/create")
    public ResponseEntity<Long> create(@RequestBody DepartmentCreateDTO departmentCreateDTO) throws CustomException {
        return ResponseEntity.ok(departmentService.create(departmentCreateDTO));
    }

    @GetMapping("/list")
    public ResponseEntity<List<DepartmentListResponseDTO>> listAll(@RequestParam Long companyId) {
        return ResponseEntity.ok().body(departmentService.listAll(companyId));
    }

    @PostMapping("/delete")
    public ResponseEntity<String> delete(@RequestParam Long departmentId) {
        departmentService.delete(departmentId);
        return ResponseEntity.ok("삭제가 완료되었습니다.");
    }

    @GetMapping("/search")
    public ResponseEntity<List<DepartmentSearchListDTO>> search(@RequestHeader("Authorization") String header,
                                                                         @RequestParam Long companyId,
                                                                         @RequestParam(defaultValue = "") String search) throws CustomException {
        return ResponseEntity.ok().body(departmentService.searchList(companyId, search));
    }

    @PostMapping("/assign-hod")
    public ResponseEntity<String> assignAdmin(@RequestParam Long userId) {
        departmentService.assignAdmin(userId);
        return ResponseEntity.ok("관리자가 지정되었습니다.");
    }

}

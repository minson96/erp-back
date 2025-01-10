package v1.erpback.company.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import v1.erpback.Exception.CustomException;
import v1.erpback.company.dto.CompanyCreateDTO;
import v1.erpback.company.dto.CompanyListRequestDTO;
import v1.erpback.company.dto.CompanyListResponseDTO;
import v1.erpback.company.service.CompanyService;
import v1.erpback.user.domain.User;
import v1.erpback.user.service.UserService;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/company")
public class CompanyController {
    private final CompanyService companyService;
    private final UserService userService;

    @GetMapping("/list")
    public ResponseEntity<List<CompanyListResponseDTO>> getCompanyList(@RequestParam String searchTerm) {
        return ResponseEntity.ok().body(companyService.list(searchTerm));
    }

    @PostMapping("/create")
    public ResponseEntity<Long> create(@ModelAttribute CompanyCreateDTO companyCreateDTO) throws CustomException, IOException {
        return ResponseEntity.ok(companyService.create(companyCreateDTO));
    }
    @GetMapping("/myCompany")
    public ResponseEntity<Long> find(@RequestHeader("Authorization") String authorizationHeader) {
        User user = userService.myInfo(authorizationHeader);
        return ResponseEntity.ok(companyService.find(user.getDepartmentId()));
    }
    @PostMapping("/delete")
    public ResponseEntity<String> delete(@RequestParam Long companyId) throws CustomException, IOException {
        companyService.delete(companyId);
        return ResponseEntity.ok("삭제가 완료되엇습니다.");
    }

    @PostMapping("/assign-admin")
    public ResponseEntity<String> assignAdmin(@RequestParam Long userId, @RequestParam Long companyId) {
        companyService.assignAdmin(userId, companyId);
        return ResponseEntity.ok("관리자가 지정되었습니다.");
    }
    @GetMapping("/logo")
    public ResponseEntity<String> getCompanyLogo(@RequestParam Long companyId) {
        return ResponseEntity.ok().body(companyService.logo(companyId));
    }


}

package v1.erpback.company.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import v1.erpback.Exception.CustomException;
import v1.erpback.Exception.ErrorCode.ErrorCode;
import v1.erpback.company.domain.Company;
import v1.erpback.company.dto.CompanyCreateDTO;
import v1.erpback.company.dto.CompanyListResponseDTO;
import v1.erpback.company.repository.CompanyMapper;
import v1.erpback.department.domain.Department;
import v1.erpback.department.repository.DepartmentMapper;
import v1.erpback.user.repository.UserMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyMapper companyMapper;
    private final DepartmentMapper departmentMapper;
    private final UserMapper userMapper;

    public List<CompanyListResponseDTO> list(String searchTerm) {
        return companyMapper.list(searchTerm);
    }

    public Long create(CompanyCreateDTO companyCreateDTO) throws CustomException, IOException {
        Company existCompany = companyMapper.findByAddress(companyCreateDTO.getName(), companyCreateDTO.getAddress());
        if (existCompany != null) {
            throw new CustomException(ErrorCode.HAS_COMPANY);
        }
        Company company = null;
        if (companyCreateDTO.getLogoImg() != null && !companyCreateDTO.getLogoImg().isEmpty()) {
            String fileName = companyCreateDTO.getLogoImg().getOriginalFilename();
            String storedFileName = System.currentTimeMillis() + "_" + fileName;
            String savePath = "C:/Users/vlrma/boardfile/test/" + storedFileName;
            companyCreateDTO.getLogoImg().transferTo(new File(savePath));

            company = Company.builder()
                    .name(companyCreateDTO.getName())
                    .address(companyCreateDTO.getAddress())
                    .originalFileName(fileName)
                    .storedFileName(storedFileName)
                    .status(Company.Status.ING)
                    .build();
            companyMapper.save(company);
        } else {
            company = Company.builder()
                    .name(companyCreateDTO.getName())
                    .address(companyCreateDTO.getAddress())
                    .status(Company.Status.ING)
                    .build();
            companyMapper.noImgSave(company);
        }
        Long companyId = company.getId();
        Department department = Department.builder()
                .parentId(null)
                .name(companyCreateDTO.getName())
                .companyId(companyId)
                .build();
        return departmentMapper.save(department);
    }

    public String logo(Long companyId) {
        return companyMapper.logo(companyId);
    }

    public void delete(Long companyId) {
        companyMapper.delete(companyId);
    }

    public Long find(Long departmentId) {
        return companyMapper.find(departmentId);
    }

    public void assignAdmin(Long userId, Long companyId) {
        if (userMapper.findAdmin(companyId) != null) {
            userMapper.findAdminChange(companyId);
        }
        companyMapper.assignAdmin(userId, companyId);
    }
}

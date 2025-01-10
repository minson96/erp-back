package v1.erpback.department.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import v1.erpback.Exception.CustomException;
import v1.erpback.Exception.ErrorCode.ErrorCode;
import v1.erpback.department.domain.Department;
import v1.erpback.department.dto.DepartmentCreateDTO;
import v1.erpback.department.dto.DepartmentListRequestDTO;
import v1.erpback.department.dto.DepartmentListResponseDTO;
import v1.erpback.department.dto.DepartmentSearchListDTO;
import v1.erpback.department.repository.DepartmentMapper;
import v1.erpback.user.domain.User;
import v1.erpback.user.dto.UserSearchListDTO;
import v1.erpback.user.repository.UserMapper;
import v1.erpback.util.PageResultDTO;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentMapper departmentMapper;
    private final UserMapper userMapper;
    private final SimpMessagingTemplate messagingTemplate;

    public Long create(DepartmentCreateDTO departmentName) throws CustomException{
        Long parentId = null;
        if (departmentName.getParentId() != null) {
            Department parent = departmentMapper.findById(departmentName.getParentId());
            if (parent == null) {
                throw new CustomException(ErrorCode.EMPTY_DATA);
            }
            parentId = departmentName.getParentId();
        }

        Department department = Department.builder()
                .name(departmentName.getName())
                .companyId(departmentName.getCompanyId())
                .parentId(parentId)
                .build();
        return departmentMapper.save(department);

    }

    public List<DepartmentListResponseDTO> listAll(Long companyId) {
        List<Department> departments = departmentMapper.findByCompanyId(companyId);
        return departments.stream()
                .map(this::buildDepartmentTree)
                .collect(Collectors.toList());
    }

    private DepartmentListResponseDTO buildDepartmentTree(Department department) {
        List<Department> subDepartments = departmentMapper.getSubDepartments2(department.getId());
        List<DepartmentListResponseDTO> subDepartmentDtos = subDepartments.stream()
                .map(this::buildDepartmentTree)
                .collect(Collectors.toList());

        return DepartmentListResponseDTO.builder()
                .id(department.getId())
                .name(department.getName())
                .parentId(department.getParentId())
                .companyId(department.getCompanyId())
                .subDepartments(subDepartmentDtos)
                .build();
    }

    public void delete(Long departmentId) {
        departmentMapper.delete(departmentId);
    }

    public List<DepartmentSearchListDTO> searchList(Long companyId, String search) {

        return departmentMapper.searchlist(companyId, search);


    }

    public List<Department> getAllSubDepartments(Long departmentId, Set<Long> visited) {
        if (visited.contains(departmentId)) {
            return Collections.emptyList();
        }
        visited.add(departmentId);

        List<Department> result = new ArrayList<>();
        List<Department> subDepartments = departmentMapper.getSubDepartments(departmentId);

        for (Department subDepartment : subDepartments) {
            if (!visited.contains(subDepartment.getId())) {
                result.add(subDepartment);
                result.addAll(getAllSubDepartments(subDepartment.getId(), visited));
            }
        }

        return result;
    }

    public Long findRootDepartment(Long companyId) {
        List<Department> department = departmentMapper.findByCompanyId(companyId);
        return department.get(0).getId();
    }

    @Transactional
    public void assignAdmin(Long userId) {
        User user = userMapper.findById(userId);
        Department department = departmentMapper.findById(user.getDepartmentId());
        if (userMapper.findHod(department.getId()) != null) {
            User hod = userMapper.findHod(department.getId());
            departmentMapper.changeHod(hod.getId());
        }
        departmentMapper.assignHod(userId);
        messagingTemplate.convertAndSend("/sub/updates/" + userId,
                "Change User Role.HOD : " + userId);
    }
}

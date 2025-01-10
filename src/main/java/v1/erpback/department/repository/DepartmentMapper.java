package v1.erpback.department.repository;

import org.apache.ibatis.annotations.*;
import v1.erpback.department.domain.Department;
import v1.erpback.department.dto.DepartmentListResponseDTO;
import v1.erpback.department.dto.DepartmentSearchListDTO;
import v1.erpback.user.domain.User;

import java.util.List;

@Mapper
public interface DepartmentMapper {
    @Insert("INSERT INTO department (name, company_id, parent_id)" +
            "VALUES (#{name}, #{companyId}, #{parentId})")
    Long save(Department department);

    @Select("SELECT * FROM department WHERE id = #{parentId}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "parentId", column = "parent_id"),
            @Result(property = "companyId", column = "company_id")
    })
    Department findById(Long parentId);

    @Select("SELECT * FROM department WHERE company_id = #{companyId} AND parent_id IS NULL")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "parentId", column = "parent_id"),
            @Result(property = "companyId", column = "company_id")
    })
    List<Department> findByCompanyId(Long companyId);

    @Select("SELECT * FROM department WHERE parent_id = #{parentId} OR id= #{parentId}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "parentId", column = "parent_id"),
            @Result(property = "companyId", column = "company_id")
    })
    List<Department> getSubDepartments(Long parentId);

    @Select("SELECT * FROM department WHERE parent_id = #{parentId}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "parentId", column = "parent_id"),
            @Result(property = "companyId", column = "company_id")
    })
    List<Department> getSubDepartments2(Long parentId);

    @Select("SELECT * FROM department WHERE name = #{parentDepartment} OR name LIKE CONCAT('%', #{search}, '%')")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "parentId", column = "parent_id"),
            @Result(property = "companyId", column = "company_id")
    })
    Department findByName(String parentDepartment);


    @Delete("DELETE FROM department WHERE id = #{departmentId}")
    void delete(Long departmentId);

    @Select("""
    SELECT
        id,
        name,
        'DEPARTMENT' AS type
    FROM
        department
    WHERE
        name LIKE CONCAT('%', #{search}, '%')
    AND company_id = #{companyId}
""")
    List<DepartmentSearchListDTO> searchlist(Long companyId, String search);

    @Select("""
    SELECT
        COUNT(*)
    FROM
        department
    WHERE
        name LIKE CONCAT('%', #{search}, '%')
""")
    long searchSize(String search);

    @Update("UPDATE user SET role='HOD' WHERE id=#{userId}")
    void assignHod(Long userId);

    @Update("UPDATE user SET role='USER' WHERE id=#{id}")
    void changeHod(Long id);
}

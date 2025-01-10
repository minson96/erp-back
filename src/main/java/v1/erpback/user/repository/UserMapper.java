package v1.erpback.user.repository;

import org.apache.ibatis.annotations.*;
import v1.erpback.user.domain.User;
import v1.erpback.user.dto.AdminsUserListDTO;
import v1.erpback.user.dto.UserProfileResponseDTO;
import v1.erpback.user.dto.UserSearchListDTO;

import java.util.List;

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM user WHERE account=#{account} AND status != 'WITHDRAWAL'")
    User findByAccount(String account);

    @Insert("INSERT INTO user (name, company_id, email, account, password, department_id, position_id, status, role, original_file_name, stored_file_name)" +
            "VALUES (#{name}, #{companyId}, #{email}, #{account}, #{password}, #{departmentId}, #{positionId}, #{status}, #{role}, #{originalFileName}, #{storedFileName})")
    Long signUp(User user);

    @Update("UPDATE user SET status = #{status} WHERE id = #{id}")
    void approve(User updateUser);

    @Select("SELECT * FROM user WHERE id=#{loginId} AND status='APPROVED'")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "account", column = "account"),
            @Result(property = "email", column = "email"),
            @Result(property = "departmentId", column = "department_id"),
            @Result(property = "positionId", column = "position_id"),
            @Result(property = "companyId", column = "company_id"),
            @Result(property = "role", column = "role"),
            @Result(property = "originalFileName", column = "original_file_name"),
            @Result(property = "storedFileName", column = "stored_file_name"),

    })
    User findById(Long loginId);

    @Select("SELECT * FROM user WHERE id=#{loginId} AND status='PENDING_APPROVAL'")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "account", column = "account"),
            @Result(property = "email", column = "email"),
            @Result(property = "departmentId", column = "department_id"),
            @Result(property = "positionId", column = "position_id"),
            @Result(property = "companyId", column = "company_id"),
            @Result(property = "role", column = "role"),
            @Result(property = "originalFileName", column = "original_file_name"),
            @Result(property = "storedFileName", column = "stored_file_name"),

    })
    User findByIdApprove(Long loginId);

    @Update("UPDATE user SET phone_number=#{phoneNumber}, company_number=#{companyNumber}, email=#{email}, department_id = #{departmentId}, position_id = #{positionId} WHERE id = #{id}")
    Long noImgUpdate(User user);

    @Update("UPDATE user SET phone_number=#{phoneNumber}, company_number=#{companyNumber}, email=#{email}, original_file_name = #{originalFileName}, department_id = #{departmentId}, position_id = #{positionId}, stored_file_name = #{storedFileName} WHERE id = #{id}")
    Long update(User user);

    @Update("UPDATE user SET password = #{password} WHERE id = #{id}")
    Long passwordReset(User user);

    @Select("SELECT id, account, name, password FROM user WHERE email=#{email} AND name=#{name}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "account", column = "account"),
            @Result(property = "name", column = "name"),
            @Result(property = "password", column = "password"),
    })
    User findByEmail(String email, String name);

    @Select("""
    WITH RECURSIVE SubDepartment AS (
        SELECT id
        FROM department
        WHERE id = #{departmentId}
        UNION ALL
        SELECT d.id
        FROM department d
        INNER JOIN SubDepartment sd ON d.parent_id = sd.id
    )
    SELECT 
        u.id, 
        u.name, 
        u.account, 
        u.email, 
        u.department_id, 
        u.position_id, 
        u.status, 
        u.phone_number,
        u.company_number,
        u.stored_file_name,
        d.name AS department_name,
        p.title AS position_name,
        u.created_at
    FROM 
        user u
    LEFT JOIN 
        department d ON u.department_id = d.id
    LEFT JOIN
        job_position p ON u.position_id = p.id
    WHERE
        (d.name LIKE CONCAT('%', #{search}, '%') OR u.name LIKE CONCAT('%', #{search}, '%'))
        AND u.department_id IN (SELECT id FROM SubDepartment)
        AND u.status != 'WITHDRAWAL'
    ORDER BY u.created_at DESC
    LIMIT #{limit} OFFSET #{offset}
""")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "account", column = "account"),
            @Result(property = "email", column = "email"),
            @Result(property = "departmentId", column = "department_id"),
            @Result(property = "departmentName", column = "department_name"),
            @Result(property = "positionName", column = "position_name"),
            @Result(property = "positionId", column = "position_id"),
            @Result(property = "status", column = "status"),
            @Result(property = "phoneNumber", column = "phone_number"),
            @Result(property = "companyNumber", column = "company_number"),
            @Result(property = "profileImg", column = "stored_file_name")
    })
    List<AdminsUserListDTO> adminsDepartmentList(@Param("departmentId") Long departmentId,
                                                 @Param("offset") int offset,
                                                 @Param("limit") int limit,
                                                 @Param("search") String search);

    @Delete("UPDATE user SET status = 'WITHDRAWAL' WHERE id = #{id}")
    void delete(User user);

    @Select("SELECT * FROM user WHERE department_id = #{departmentId} AND status='APPROVED'")
    List<User> findByDepartmentId(Long departmentId);

    @Select("""
    WITH RECURSIVE SubDepartment AS (
        SELECT id
        FROM department
        WHERE id = #{departmentId}
        UNION ALL
        SELECT d.id
        FROM department d
        INNER JOIN SubDepartment sd ON d.parent_id = sd.id
    )
    SELECT 
        COUNT(*)
    FROM 
        user u
    LEFT JOIN 
        department d ON u.department_id = d.id
    LEFT JOIN
        job_position p ON u.position_id = p.id
    WHERE
        (d.name LIKE CONCAT('%', #{search}, '%') OR u.name LIKE CONCAT('%', #{search}, '%'))
        AND u.department_id IN (SELECT id FROM SubDepartment)
        AND u.status != 'WITHDRAWAL'
""")
    long countContent(@Param("departmentId") Long departmentId, @Param("search") String search);


    @Select("""
    SELECT 
        u.id, 
        u.name, 
        u.account, 
        u.email, 
        u.department_id, 
        u.position_id, 
        u.status,
        u.role,
        u.phone_number,
        u.company_number,
        u.stored_file_name,
        d.name AS department_name,
        p.title AS position_name,
        u.company_id
    FROM 
        user u
    LEFT JOIN 
        department d ON u.department_id = d.id
    LEFT JOIN
        job_position p ON u.position_id = p.id
    WHERE
        u.id = #{id}
""")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "account", column = "account"),
            @Result(property = "email", column = "email"),
            @Result(property = "departmentId", column = "department_id"),
            @Result(property = "departmentName", column = "department_name"),
            @Result(property = "positionName", column = "position_name"),
            @Result(property = "positionId", column = "position_id"),
            @Result(property = "status", column = "status"),
            @Result(property = "role", column = "role"),
            @Result(property = "phoneNumber", column = "phone_number"),
            @Result(property = "companyNumber", column = "company_number"),
            @Result(property = "storedFileName", column = "stored_file_name"),
            @Result(property = "companyId", column = "company_id")
    })
    UserProfileResponseDTO userInfo(Long id);

    @Select("""
    SELECT
        id,
        name,
        account,
        role,
        'USER' AS type
    FROM
        user
    WHERE
        name LIKE CONCAT('%', #{search}, '%') AND status != 'WITHDRAWAL' AND id != #{id} AND company_id=#{companyId}
""")
    List<UserSearchListDTO> searchlist(Long companyId, Long id, String search);

    @Select("""
    SELECT
        COUNT(*)
    FROM
        user
    WHERE
        name LIKE CONCAT('%', #{search}, '%') AND status != 'WITHDRAWAL'
""")
    long searchSize(String search);

    @Select("SELECT * FROM user WHERE email=#{email} AND name=#{name} AND account=#{account}")
    User findByUser(String account, String email, String name);

    @Select("""
    SELECT
        id,
        name,
        account,
        role,
        'USER' AS type
    FROM
        user
    WHERE
        name LIKE CONCAT('%', #{search}, '%') AND status != 'WITHDRAWAL' AND id != #{id} AND department_id=#{departmentId}
""")
    List<UserSearchListDTO> departInSearchlist(Long departmentId, Long id, String search);

    @Select("SELECT * FROM user WHERE department_id = #{departmentId} AND role = 'HOD'")
    User findHod(Long departmentId);

    @Update("UPDATE user SET role='USER' WHERE companyId=#{companyId} AND role='ADMIN'")
    void findAdminChange(Long companyId);

    @Select("SELECT * FROM user WHERE company_id = #{companyId} AND role = 'ADMIN'")
    User findAdmin(Long companyId);
}
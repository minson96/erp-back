package v1.erpback.company.repository;

import org.apache.ibatis.annotations.*;
import v1.erpback.company.domain.Company;
import v1.erpback.company.dto.CompanyListResponseDTO;

import java.util.List;

@Mapper
public interface CompanyMapper {

    @Select("""
    SELECT id, name, address
    FROM company
    WHERE (name LIKE CONCAT('%', #{searchTerm}, '%') 
           OR address LIKE CONCAT('%', #{searchTerm}, '%'))
      AND status != 'DELETE'
""")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "address", column = "address")
    })
    List<CompanyListResponseDTO> list(@Param("searchTerm")String searchTerm);

    @Insert("INSERT INTO company (name, status, address, original_file_name, stored_file_name)" +
            "VALUES (#{name}, #{status},#{address}, #{originalFileName}, #{storedFileName})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    Long save(Company company);

    @Select("SELECT * FROM company WHERE name = #{name} AND address = #{address}")
    Company findByAddress(String name, String address);

    @Insert("INSERT INTO company (name, status, address)" +
            "VALUES (#{name}, #{status}, #{address})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    Long noImgSave(Company company);

    @Select("SELECT stored_file_name FROM company WHERE id = #{id}")
    String logo(Long id);

    @Update("UPDATE company SET status='DELETE' WHERE id=#{companyId}")
    void delete(Long companyId);

//    @Delete("DELETE FROM company WHERE id=#{companyId}")
//    void delete(Long companyId);

    @Select("SELECT c.id FROM company c LEFT JOIN department d ON d.company_id = c.id WHERE d.id = #{departmentId}")
    Long find(Long departmentId);

    @Update("UPDATE user SET role='ADMIN' WHERE id=#{userId}")
    void assignAdmin(Long userId, Long companyId);
}

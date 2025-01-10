package v1.erpback.position.repository;

import org.apache.ibatis.annotations.*;
import v1.erpback.position.domain.JobPosition;

import java.util.List;

@Mapper
public interface PositionMapper {
    @Insert("INSERT INTO job_position (title, company_id) VALUES (#{title}, #{companyId})")
    Long create(Long companyId, String title);

    @Select("SELECT * FROM job_position WHERE company_id = #{companyId}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "title", column = "title"),
            @Result(property = "companyId", column = "company_id")
    })
    List<JobPosition> list(Long companyId);

    @Delete("DELETE FROM job_position WHERE id=#{positionId} AND company_id=#{companyId}")
    void delete(Long companyId, Long positionId);
}

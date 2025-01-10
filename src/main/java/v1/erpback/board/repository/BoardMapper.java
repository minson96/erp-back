package v1.erpback.board.repository;

import org.apache.ibatis.annotations.*;
import v1.erpback.board.domain.Board;

import java.util.List;

@Mapper
public interface BoardMapper {

    @Insert("INSERT INTO board (name, company_id) VALUES (#{name}, #{companyId})")
    Long create(Board board);

    @Select("SELECT * FROM board WHERE company_id = #{companyId}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "companyId", column = "company_id")
    })
    List<Board> list(Long companyId);

    @Update("UPDATE board SET name=#{name} WHERE id=#{id}")
    Long update(Board board);

    @Delete("DELETE FROM board WHERE id=#{id}")
    Long delete(Long id);
}

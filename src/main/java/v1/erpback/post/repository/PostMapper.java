package v1.erpback.post.repository;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.StringTypeHandler;
import v1.erpback.post.domain.Post;
import v1.erpback.post.dto.PostDetailDTO;
import v1.erpback.post.dto.PostListDTO;

import java.util.List;

@Mapper
public interface PostMapper {
    @Insert("INSERT INTO post (title, content, board_id, user_id)" +
            "VALUES (#{title}, #{content}, #{boardId}, #{userId})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    Long create(Post post);

    @Select("""
    SELECT
        p.id,
        b.name AS board_name,
        p.title,
        p.created_at
    FROM
        post p
    INNER JOIN
        board b ON p.board_id = b.id
    WHERE
        (p.title LIKE CONCAT('%', #{search}, '%') OR b.name LIKE CONCAT('%', #{search}, '%'))
        AND b.company_id = #{companyId}
    ORDER BY
        p.created_at DESC
    LIMIT #{limit} OFFSET #{offset}
""")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "title", column = "title"),
            @Result(property = "boardName", column = "board_name"),
            @Result(property = "createdAt", column = "created_at")
    })
    List<PostListDTO> list(@Param("companyId") Long companyId,
                            @Param("offset") int offset,
                           @Param("limit") int limit,
                           @Param("search") String search);

    @Select("""
    <script>
    SELECT
        COUNT(*)
    FROM
        post p
    INNER JOIN
        board b ON p.board_id = b.id
    WHERE
        (p.title LIKE CONCAT('%', #{search}, '%') OR b.name LIKE CONCAT('%', #{search}, '%'))
        AND b.company_id = #{companyId}
    </script>
""")
    long countList(Long companyId, String search);

    @Select("""
    <script>
    SELECT
        p.id,
        p.title,
        b.name AS board_name,
        p.content,
        GROUP_CONCAT(f.stored_file_name) AS stored_file_names
    FROM
        post p
    LEFT JOIN
        board b ON p.board_id = b.id
    LEFT JOIN
        file f ON p.id = f.post_id
    WHERE
        p.id = #{postId}
    GROUP BY
            p.id
    </script>
""")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "title", column = "title"),
            @Result(property = "boardName", column = "board_name"),
            @Result(property = "content", column = "content"),
            @Result(property = "storedFileNames", column = "stored_file_names", javaType = List.class, typeHandler = StringTypeHandler.class)
    })
    PostDetailDTO detail(Long postId);

    @Delete("""
    <script>
    DELETE FROM
        post
    WHERE
        id = #{id}
    </script>
""")
    void delete(Long id);

    @Update("UPDATE post SET title = #{title}, content = #{content}, board_id = #{boardId} WHERE id = #{id}")
    void update(Post post);
}

package v1.erpback.vote.repository;

import org.apache.ibatis.annotations.*;
import v1.erpback.user.domain.User;
import v1.erpback.vote.domain.Vote;
import v1.erpback.vote.domain.VoteResult;
import v1.erpback.vote.dto.VoteDetailDTO;
import v1.erpback.vote.dto.VoteListDTO;

import java.util.List;

@Mapper
public interface VoteMapper {
    @Insert("INSERT INTO vote (title, start_date_time, end_date_time, user_id, status)" +
            "VALUES (#{title}, #{startDateTime}, #{endDateTime}, #{userId}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    Long create(Vote vote);

    @Select("""
    SELECT
        v.id,
        v.title,
        v.end_date_time
    FROM
        vote v
    LEFT JOIN vote_user vu ON v.id=vu.vote_id
    WHERE
        v.title LIKE CONCAT('%', #{search}, '%') AND v.status = 'PENDING' AND (vu.department_id = #{user.departmentId} OR vu.user_id = #{user.id})
    ORDER BY
        v.end_date_time DESC 
    LIMIT #{limit} OFFSET #{offset}
""")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "title", column = "title"),
            @Result(property = "endDateTime", column = "end_date_time")
    })
    List<VoteListDTO> list(@Param("user") User user, @Param("offset")int offset, @Param("limit")int limit, @Param("search")String search);

    @Select("""
    SELECT
        COUNT(*)
    FROM
        vote v
    LEFT JOIN vote_user vu ON v.id=vu.vote_id
    WHERE
        v.title LIKE CONCAT('%', #{search}, '%') AND v.status = 'PENDING' AND (vu.department_id = #{user.departmentId} OR vu.user_id = #{user.id})
""")
    long listCount(@Param("user") User user, @Param("search")String search);

    @Update("UPDATE vote SET status = 'END' WHERE id = #{voteId}")
    void voteEnd(Long voteId);

    @Select("""
    SELECT
        v.id,
        v.title,
        v.end_date_time
    FROM
        vote v
    LEFT JOIN vote_user vu ON v.id=vu.vote_id
    WHERE
        v.title LIKE CONCAT('%', #{search}, '%') AND v.status = 'END' AND (vu.department_id = #{user.departmentId} OR vu.user_id = #{user.id})
    ORDER BY
        v.end_date_time DESC 
    LIMIT #{limit} OFFSET #{offset}
""")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "title", column = "title"),
            @Result(property = "endDateTime", column = "end_date_time")
    })
    List<VoteListDTO> resultList(@Param("user") User user, @Param("offset") int offset, @Param("limit") int limit, @Param("search") String search);

    @Select("""
    SELECT
        COUNT(*)
    FROM
        vote v
    LEFT JOIN vote_user vu ON v.id=vu.vote_id
    WHERE
        v.title LIKE CONCAT('%', #{search}, '%') AND v.status = 'END' AND (vu.department_id = #{user.departmentId} OR vu.user_id = #{user.id})
""")
    long resultListCount(@Param("user") User user, @Param("search")String search);


    @Select("""
    SELECT
        v.id,
        v.title,
        v.end_date_time
    FROM
        vote v
    LEFT JOIN vote_user vu ON v.id=vu.vote_id
    WHERE
        v.title LIKE CONCAT('%', #{search}, '%') AND (vu.department_id = #{user.departmentId} OR vu.user_id = #{user.id})
    ORDER BY
        v.end_date_time DESC 
    LIMIT #{limit} OFFSET #{offset}
""")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "title", column = "title"),
            @Result(property = "endDateTime", column = "end_date_time")
    })
    List<VoteListDTO> myList(@Param("user") User user, @Param("offset") int offset, @Param("limit") int limit, @Param("search") String search);

    @Select("""
    SELECT
        COUNT(*)
    FROM
        vote v
    LEFT JOIN vote_user vu ON v.id=vu.vote_id
    WHERE
        v.title LIKE CONCAT('%', #{search}, '%') AND v.status = 'END' AND (vu.department_id = #{user.departmentId} OR vu.user_id = #{user.id})
""")
    long myListCount(@Param("user") User user, @Param("search")String search);

    @Select("""
    SELECT
        v.id,
        u.name AS user_name,
        v.title,
        v.status,
        v.start_date_time,
        v.end_date_time,
        GROUP_CONCAT(DISTINCT vr.answer) AS answers, -- 전체 답변
        GROUP_CONCAT(vr.answer_count) AS answer_count, -- 답변별 카운트
        GROUP_CONCAT(DISTINCT u2.name SEPARATOR ', ') AS userNames,
        GROUP_CONCAT(DISTINCT d.name SEPARATOR ', ') AS departmentNames,
        (
            SELECT vr_selected.answer
            FROM vote_result vr_selected
            JOIN vote_user vu_selected ON vr_selected.id = vu_selected.vote_result_id
            WHERE vr_selected.vote_id = v.id
              AND vu_selected.user_id = #{userId} -- 특정 사용자의 선택
              AND vu_selected.vote_id = v.id
            LIMIT 1 -- 단일 결과 보장
        ) AS selected_answer -- 사용자가 선택한 답변
    FROM
        vote v
    LEFT JOIN user u ON u.id = v.user_id
    LEFT JOIN vote_result vr ON vr.vote_id = v.id
    LEFT JOIN vote_user vu ON vu.vote_id = v.id
    LEFT JOIN department d ON d.id = vu.department_id
    LEFT JOIN user u2 ON u2.id = vu.user_id
    WHERE
        v.id = #{voteId}
    GROUP BY
        v.id, v.user_id, u.name, v.title, v.status, v.start_date_time, v.end_date_time
""")

    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "title", column = "title"),
            @Result(property = "userName", column = "user_name"),
            @Result(property = "status", column = "status"),
            @Result(property = "startDateTime", column = "start_date_time"),
            @Result(property = "endDateTime", column = "end_date_time"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "answers", column = "answers"),
            @Result(property = "answersCount", column = "answer_count"),
            @Result(property = "userSelect", column = "selected_answer"),
            @Result(property = "userNames", column = "userNames"),
            @Result(property = "departmentNames", column = "departmentNames")
    })
    VoteDetailDTO detail(Long userId, Long voteId);

    @Select("""
    SELECT * 
    FROM vote
    WHERE end_date_time <= NOW()
""")
    List<Vote> timeoverList();
}

package v1.erpback.vote.repository;

import org.apache.ibatis.annotations.*;
import v1.erpback.vote.domain.VoteUser;

@Mapper
public interface VoteUserMapper {
    @Insert("""
INSERT IGNORE INTO vote_user (department_id, vote_id, type) VALUES (#{departmentId}, #{voteId}, #{type})
""")
    void voteCreateDepartment(Long departmentId, Long voteId, String type);

    @Insert("INSERT INTO vote_user (user_id, vote_id, type) VALUES (#{userId}, #{voteId}, #{type})")
    void voteCreateUser(Long userId, Long voteId, String type);

    @Update("UPDATE vote_user SET vote_result_id = #{choice} WHERE id = #{voteId}")
    boolean vote(Long voteId, Long choice);

    @Select("""
    SELECT
        id,
        user_id,
        vote_id,
        department_id,
        vote_result_id
    FROM
        vote_user
    WHERE
        user_id = #{id} AND vote_id = #{voteId}
            """)
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "departmentId", column = "department_id"),
            @Result(property = "voteResultId", column = "vote_result_id")
    })
    VoteUser isVoted(Long id, Long voteId);

    @Select("""
    SELECT 
        COUNT(*)
    FROM
        vote_user
    WHERE
        vote_id = #{voteId} AND vote_result_id = #{choice}
""")
    Long countUser(Long voteId, Long choice);
}

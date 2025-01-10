package v1.erpback.vote.repository;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import v1.erpback.vote.domain.VoteResult;
import v1.erpback.vote.domain.VoteUser;

import java.util.List;

@Mapper
public interface VoteResultMapper {
    @Insert("INSERT INTO vote_result (vote_id, answer) VALUES (#{voteId}, #{answer})")
    void create(VoteResult voteResult);

    @Select("""
    SELECT 
        *
    FROM
        vote_result
    WHERE
        vote_id = #{voteId}
""")
    List<VoteResult> countAnswer(Long voteId);



    @Update("UPDATE vote_result SET answer_count = #{userCount} WHERE vote_id = #{voteId} AND id = #{voteResultId}")
    void result(Long voteId, Long userCount, Long voteResultId);

    @Select("SELECT * FROM vote_result WHERE vote_id = #{voteId} AND answer = #{answer}")
    VoteResult findAnswer(Long voteId,String answer);

    @Select("""
    SELECT * 
    FROM vote_result vr
    JOIN vote v ON vr.vote_id = v.id
    WHERE v.end_date_time <= NOW()
""")
    List<VoteResult> voteEndList();
}

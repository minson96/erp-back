package v1.erpback.noti.repository;

import org.apache.ibatis.annotations.*;
import v1.erpback.noti.domain.Noti;

import java.util.List;

@Mapper
public interface NotiMapper {
    @Select("SELECT id, user_id, message, created_at, read_at FROM noti WHERE user_id = #{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "message", column = "message"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "readAt", column = "read_at")
    })
    List<Noti> list(Long id);

    @Insert("INSERT INTO noti (user_id, message, read_at) VALUES (#{id}, #{message}, false)")
    void save(Long id, String message);

    @Update("UPDATE noti SET read_at = true WHERE user_id=#{id}")
    void read(Long id);

    @Select("SELECT * FROM noti WHERE id=#{notiId}")
    Noti findNoti(Long notiId);
}

package v1.erpback.user.repository;

import org.apache.ibatis.annotations.*;
import v1.erpback.user.dto.UserLoginListDTO;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface LoginMapper {

    @Select("""
    SELECT id, ip, user_agent, os, login_time FROM login_log WHERE user_id = #{id} AND (login_time LIKE CONCAT('%', #{search}, '%'))
    LIMIT #{limit} OFFSET #{offset}
""")
    @Results({
            @Result(property = "id",column = "id"),
            @Result(property = "ip",column = "ip"),
            @Result(property = "userAgent",column = "user_agent"),
            @Result(property = "os",column = "os"),
            @Result(property = "loginTime",column = "login_time")
    })
    List<UserLoginListDTO> list(@Param("id") Long id,
                                @Param("offset") int offset,
                                @Param("limit") int limit,
                                @Param("search") String search);

    @Insert("INSERT INTO login_log (user_id, ip, user_agent, os, login_time)" +
            "VALUES (#{userId}, #{ip}, #{userAgent}, #{os}, #{loginTime})")
    void save(Long userId, String ip, String userAgent, String os, LocalDateTime loginTime);

    @Select("""
    SELECT COUNT(*) FROM login_log WHERE user_id = #{id} AND (login_time LIKE CONCAT('%', #{search}, '%'))
""")
    long loginSize(Long id, String search);
}

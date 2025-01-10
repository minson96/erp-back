package v1.erpback.schedule.repository;

import org.apache.ibatis.annotations.*;
import org.springframework.http.ResponseEntity;
import v1.erpback.schedule.domain.Schedule;
import v1.erpback.schedule.dto.ScheduleDetailDTO;
import v1.erpback.schedule.dto.ScheduleListDTO;

import java.util.List;

@Mapper
public interface ScheduleMapper {
    @Select("""
    SELECT 
        s.id,
        s.start_date_time AS startDateTime,
        s.end_date_time AS endDateTime,
        s.title,
        s.place,
        s.content,
        u2.name AS name,
        GROUP_CONCAT(DISTINCT u.name SEPARATOR ', ') AS userNames,
        GROUP_CONCAT(DISTINCT d.name SEPARATOR ', ') AS departmentNames,
        GROUP_CONCAT(DISTINCT f.stored_file_name) AS files
    FROM 
        schedule s
    LEFT JOIN 
        schedule_user su ON s.id = su.schedule_id
    LEFT JOIN 
        user u ON su.user_id = u.id
    LEFT JOIN 
        department d ON su.department_id = d.id
    LEFT JOIN 
        user u2 ON s.user_id = u2.id
    LEFT JOIN 
        file f ON s.id = f.schedule_id
    WHERE 
        s.id = #{id}
    GROUP BY 
        s.id, s.start_date_time, s.end_date_time, s.title, s.place, s.content, u2.name
""")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "startDateTime", column = "startDateTime"),
            @Result(property = "endDateTime", column = "endDateTime"),
            @Result(property = "title", column = "title"),
            @Result(property = "name", column = "name"),
            @Result(property = "userNames", column = "userNames"),
            @Result(property = "departmentNames", column = "departmentNames"),
            @Result(property = "place", column = "place"),
            @Result(property = "content", column = "content"),
            @Result(property = "files", column = "files")
    })
    ScheduleDetailDTO detail(Long id);

    @Insert("INSERT INTO schedule_user (schedule_id, department_id, schedule_type)" +
            "VALUES (#{id}, #{departmentId}, #{department})")
    Long createDepartment(Long id, Long departmentId, String department);

    @Insert("INSERT INTO schedule_user (schedule_id, user_id, schedule_type)" +
            "VALUES (#{id}, #{userId}, #{user})")
    Long createUser(Long id, Long userId, String user);

    @Insert("INSERT INTO schedule (user_id,start_date_time, end_date_time, title, place, content)" +
            "VALUES (#{userId},#{startDateTime}, #{endDateTime}, #{title}, #{place}, #{content})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    Long createSchedule(Schedule toSchedule);

    @Select("""
    SELECT 
        s.id,
        s.start_date_time AS startDateTime,
        s.end_date_time AS endDateTime,
        s.title,
        s.place,
        s.content,
        u2.name AS name,
        GROUP_CONCAT(DISTINCT u.name SEPARATOR ', ') AS userNames,
        GROUP_CONCAT(DISTINCT d.name SEPARATOR ', ') AS departmentNames,
        GROUP_CONCAT(DISTINCT f.stored_file_name) AS files
    FROM 
        schedule s
    LEFT JOIN 
        schedule_user su ON s.id = su.schedule_id
    LEFT JOIN 
        user u ON su.user_id = u.id
    LEFT JOIN 
        department d ON su.department_id = d.id
    LEFT JOIN 
        user u2 ON s.user_id = u2.id
    LEFT JOIN 
        file f ON s.id = f.schedule_id
    WHERE
        su.department_id = #{departmentId} OR su.user_id = #{userId}
    GROUP BY 
        s.id, s.start_date_time, s.end_date_time, s.title, s.place, s.content, u2.name
    ORDER BY
        s.start_date_time DESC
    LIMIT #{limit} OFFSET #{offset}
""")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "startDateTime", column = "startDateTime"),
            @Result(property = "endDateTime", column = "endDateTime"),
            @Result(property = "title", column = "title"),
            @Result(property = "name", column = "name"),
            @Result(property = "userNames", column = "userNames"),
            @Result(property = "departmentNames", column = "departmentNames"),
            @Result(property = "place", column = "place"),
            @Result(property = "content", column = "content"),
            @Result(property = "files", column = "files")
    })
    List<ScheduleListDTO> list(@Param("userId") Long id,
                               @Param("departmentId") Long departmentId,
                               @Param("offset") int offset,
                               @Param("limit") int limit);

    @Select("""
    SELECT 
        COUNT(DISTINCT s.id)
    FROM 
        schedule s
    LEFT JOIN 
        schedule_user su ON s.id = su.schedule_id
    LEFT JOIN 
        user u ON su.user_id = u.id
    LEFT JOIN 
        department d ON su.department_id = d.id
    LEFT JOIN 
        user u2 ON s.user_id = u2.id
    LEFT JOIN 
        file f ON s.id = f.schedule_id
    WHERE
        su.department_id = #{departmentId} OR su.user_id = #{userId}
""")
    long listSize(@Param("userId") Long id,
                  @Param("departmentId") Long departmentId);

    @Select("SELECT * FROM schedule_user WHERE user_id=#{id} AND schedule_id=#{scheduleId}")
    Schedule findByScheduleIdAndMe(Long scheduleId, Long id);
}

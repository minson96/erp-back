package v1.erpback.attendance.repository;

import org.apache.ibatis.annotations.*;
import v1.erpback.attendance.domain.Attendance;
import v1.erpback.attendance.dto.AttendanceListDTO;
import v1.erpback.attendance.dto.AttendanceSaveDTO;
import v1.erpback.user.domain.User;

import java.util.List;

@Mapper
public interface AttendanceMapper {

    @Insert("INSERT INTO attendance (start_date_time, end_date_time, reason, description, department_id, status, user_id)" +
    "VALUES (#{attendance.startDateTime}, #{attendance.endDateTime}, #{attendance.reason}, #{attendance.description}, #{attendance.departmentId}, #{attendance.status}, #{id})")
    void save(AttendanceSaveDTO attendance, Long id);

    @Select("""
    SELECT
        a.id,
        a.start_date_time,
        a.end_date_time,
        a.reason,
        a.description,
        a.department_id,
        a.status,
        u.name
    FROM
        attendance a
    LEFT JOIN
            user u ON a.user_id=u.id
    WHERE
        a.department_id = #{departmentId}
        AND a.reason LIKE CONCAT('%', #{search}, '%') 
    ORDER BY
        a.start_date_time DESC
    LIMIT #{size} OFFSET #{offset}
""")
    @Results({
            @Result(property = "name", column = "name"),
            @Result(property = "id", column = "id"),
            @Result(property = "status", column = "status"),
            @Result(property = "startDateTime", column = "start_date_time"),
            @Result(property = "endDateTime", column = "end_date_time"),
            @Result(property = "reason", column = "reason"),
            @Result(property = "description", column = "description"),
            @Result(property = "departmentId", column = "department_id"),
    })
    List<AttendanceListDTO> attendanceList(Long departmentId, int offset, int size, String search);

    @Select("""
    SELECT
        COUNT(*)
    FROM
        attendance a
    WHERE
        a.department_id = #{departmentId}
        AND a.reason LIKE CONCAT('%', #{search}, '%') 
""")
    long attendanceListSize(Long departmentId, String search);

    @Update("UPDATE attendance SET status='APPROVE' WHERE id = #{attendanceId}")
    void approve(Long attendanceId);

    @Update("UPDATE attendance SET status='DENY' WHERE id = #{attendanceId}")
    void deny(Long attendanceId);


    @Select("""
    SELECT
        a.id,
        a.start_date_time,
        a.end_date_time,
        a.reason,
        a.description,
        a.department_id,
        a.status,
        u.name
    FROM
        attendance a
    LEFT JOIN
            user u ON a.user_id=u.id
    WHERE
        a.department_id = #{departmentId}
        AND a.reason LIKE CONCAT('%', #{search}, '%') AND a.status = 'WAIT'
    ORDER BY
        a.start_date_time DESC
    LIMIT #{size} OFFSET #{offset}
""")
    @Results({
            @Result(property = "name", column = "name"),
            @Result(property = "id", column = "id"),
            @Result(property = "status", column = "status"),
            @Result(property = "startDateTime", column = "start_date_time"),
            @Result(property = "endDateTime", column = "end_date_time"),
            @Result(property = "reason", column = "reason"),
            @Result(property = "description", column = "description"),
            @Result(property = "departmentId", column = "department_id"),
    })
    List<AttendanceListDTO> waitList(Long departmentId, int offset, int size, String search);

    @Select("""
    SELECT
        COUNT(*)
    FROM
        attendance a
    WHERE
        a.department_id = #{departmentId}
        AND a.reason LIKE CONCAT('%', #{search}, '%') AND a.status = 'WAIT'
""")
    long waitListSize(Long departmentId, String search);

    @Select("""
    SELECT
        a.id,
        a.start_date_time,
        a.end_date_time,
        a.reason,
        a.description,
        a.department_id,
        a.status
    FROM
        attendance a
    WHERE
        a.user_id = #{id}
        AND a.reason LIKE CONCAT('%', #{search}, '%')
    ORDER BY
        a.start_date_time DESC
    LIMIT #{size} OFFSET #{offset}
""")
    @Results({
            @Result(property = "name", column = "name"),
            @Result(property = "id", column = "id"),
            @Result(property = "status", column = "status"),
            @Result(property = "startDateTime", column = "start_date_time"),
            @Result(property = "endDateTime", column = "end_date_time"),
            @Result(property = "reason", column = "reason"),
            @Result(property = "description", column = "description"),
            @Result(property = "departmentId", column = "department_id"),
    })
    List<AttendanceListDTO> myList(Long id, int offset, int size, String search);

    @Select("""
    SELECT
        COUNT(*)
    FROM
        attendance a
    WHERE
        a.user_id = #{id}
        AND a.reason LIKE CONCAT('%', #{search}, '%')
""")
    long myListSize(Long id, String search);

    @Select("""
        SELECT u.*
        FROM user u
        LEFT JOIN attendance a 
            ON u.id = a.user_id 
            AND DATE(a.start_date_time) = CURDATE()
        WHERE a.id IS NULL AND u.company_id IS NOT NULL
    """)
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "departmentId", column = "department_id"),
            @Result(property = "positionId", column = "position_id"),
            @Result(property = "companyId", column = "company_id")
    })
    List<User> getAbsentUsers();

    @Update("UPDATE attendance SET end_date_time=#{endDateTime}, reason=#{reason} WHERE id=#{attendanceId}")
    void leaveWork(Long attendanceId, String endDateTime, Attendance.Reason reason);

    @Select("SELECT user_id, reason FROM attendance WHERE id=#{attendanceId}")
    @Results({
            @Result(property = "userId", column = "user_id"),@Result(property = "reason", column = "reason")
    })
    Attendance find(Long attendanceId);
}

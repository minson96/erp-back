package v1.erpback.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import v1.erpback.attendance.domain.Attendance;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceListDTO {

    private Long id;
    private String name;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Attendance.Reason reason;
    private String description;
    private Long departmentId;
    private Attendance.Status status;
}

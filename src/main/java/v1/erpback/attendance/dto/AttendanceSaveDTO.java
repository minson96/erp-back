package v1.erpback.attendance.dto;

import lombok.*;
import v1.erpback.attendance.domain.Attendance;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceSaveDTO {
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Attendance.Reason reason;
    private String description;
    private Long departmentId;
    private Attendance.Status status;

}


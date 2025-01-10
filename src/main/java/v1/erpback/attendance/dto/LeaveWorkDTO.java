package v1.erpback.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import v1.erpback.attendance.domain.Attendance;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeaveWorkDTO {
    private Long attendanceId;
    private Attendance.Reason reason;
    private String endDateTime;
}

package v1.erpback.attendance.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attendance {
    public enum Reason{
        TARDY_AND_LEAVING_EARLY, ATTENDANCE, VACATION, BUSINESS_TRIP, TARDINESS, WORKING_OUTSIDE, LEAVING_EARLY, ABSENTEEISM
    }
    public enum Status{
        WAIT, APPROVE, DENY, NOT_APPLICABLE
    }
    private Long id;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Reason reason;
    private String description;
    private Long departmentId;
    private Status status;
    private Long userId;
}

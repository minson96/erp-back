package v1.erpback.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleCreateRequest {
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String title;
    private List<Long> userId;
    private String account;
    private List<Long> departmentId;
    private String content;
    private String place;
}

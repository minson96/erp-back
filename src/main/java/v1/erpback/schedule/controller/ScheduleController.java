package v1.erpback.schedule.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import v1.erpback.schedule.domain.Schedule;
import v1.erpback.schedule.dto.ScheduleCreateRequest;
import v1.erpback.schedule.dto.ScheduleDetailDTO;
import v1.erpback.schedule.dto.ScheduleListDTO;
import v1.erpback.schedule.service.ScheduleService;
import v1.erpback.user.domain.User;
import v1.erpback.user.service.UserService;
import v1.erpback.util.PageResultDTO;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/schedule")
public class ScheduleController {
    private final ScheduleService scheduleService;
    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestHeader("Authorization")String header, @RequestBody ScheduleCreateRequest schedule) throws IOException {
        User user = userService.myInfo(header);

        scheduleService.create(user.getId() ,schedule);
        return ResponseEntity.ok("생성이 완료되었습니다.");
    }

    @GetMapping("/detail")
    public ResponseEntity<ScheduleDetailDTO> detail(@RequestHeader("Authorization") String header, @RequestParam("id") Long id) throws IOException {
        User user = userService.myInfo(header);
        return ResponseEntity.ok(scheduleService.detail(id));
    }

    @GetMapping("/list")
    public ResponseEntity<PageResultDTO<ScheduleListDTO>> list(@RequestHeader("Authorization") String header,
                                                               @RequestParam(defaultValue = "1") int page,
                                                               @RequestParam(defaultValue = "10") int size) {
        User user = userService.myInfo(header);
        return ResponseEntity.ok().body(scheduleService.list(user.getId(),user.getDepartmentId(), page, size));
    }
}

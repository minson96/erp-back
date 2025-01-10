package v1.erpback.attendance.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import v1.erpback.attendance.domain.Attendance;
import v1.erpback.attendance.dto.AttendanceListDTO;
import v1.erpback.attendance.dto.AttendanceSaveDTO;
import v1.erpback.attendance.dto.LeaveWorkDTO;
import v1.erpback.attendance.service.AttendanceService;
import v1.erpback.user.domain.User;
import v1.erpback.user.service.UserService;
import v1.erpback.util.PageResultDTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/attendance")
public class AttendanceController {
    private final AttendanceService attendanceService;
    private final UserService userService;

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestHeader("Authorization")String header, @RequestBody AttendanceSaveDTO attendance) {
        User user = userService.myInfo(header);
        attendanceService.save(attendance, user);
        return ResponseEntity.ok("신청이 완료되었습니다.");
    }

    @GetMapping("/list")
    public ResponseEntity<PageResultDTO<AttendanceListDTO>> list(@RequestHeader("Authorization")String header,
                                                          @RequestParam(defaultValue = "1") int page,
                                                          @RequestParam(defaultValue = "10") int size,
                                                          @RequestParam(defaultValue = "") String search) {
        User user = userService.myInfo(header);
        return ResponseEntity.ok().body(attendanceService.list(user, page, size, search));
    }

    @GetMapping("/waitList")
    public ResponseEntity<PageResultDTO<AttendanceListDTO>> waitList(@RequestHeader("Authorization")String header,
                                                          @RequestParam(defaultValue = "1") int page,
                                                          @RequestParam(defaultValue = "10") int size,
                                                          @RequestParam(defaultValue = "") String search) {
        User user = userService.myInfo(header);
        return ResponseEntity.ok().body(attendanceService.waitList(user, page, size, search));
    }

    @GetMapping("/myList")
    public ResponseEntity<PageResultDTO<AttendanceListDTO>> myList(@RequestHeader("Authorization")String header,
                                                                   @RequestParam(defaultValue = "1") int page,
                                                                   @RequestParam(defaultValue = "10") int size,
                                                                   @RequestParam(defaultValue = "") String search) {
        User user = userService.myInfo(header);
        return ResponseEntity.ok().body(attendanceService.myList(user, page, size, search));
    }


    @PostMapping("/approve")
    public ResponseEntity<String> approve(@RequestHeader("Authorization")String header, @RequestParam Long attendanceId) {
        User user = userService.myInfo(header);
        attendanceService.approve(user.getName(), attendanceId);
        return ResponseEntity.ok("승인되었습니다.");
    }

    @PostMapping("/deny")
    public ResponseEntity<String> deny(@RequestHeader("Authorization")String header, @RequestParam Long attendanceId) {
        User user = userService.myInfo(header);
        attendanceService.deny(user.getName(), attendanceId);
        return ResponseEntity.ok("반려되었습니다.");
    }

    @PostMapping("/leaveWork")
    public ResponseEntity<String> leaveWork(@RequestBody LeaveWorkDTO leaveWorkDTO) {
        LocalDateTime now = LocalDateTime.now();

        // MySQL 호환 형식으로 변환 (YYYY-MM-DD HH:mm:ss)
        String endDateTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        attendanceService.leaveWork(leaveWorkDTO.getAttendanceId(), endDateTime, leaveWorkDTO.getReason());
        return ResponseEntity.ok("퇴근 처리되었습니다.");
    }
}

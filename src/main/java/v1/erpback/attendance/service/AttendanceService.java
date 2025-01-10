package v1.erpback.attendance.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import v1.erpback.attendance.domain.Attendance;
import v1.erpback.attendance.dto.AttendanceListDTO;
import v1.erpback.attendance.dto.AttendanceSaveDTO;
import v1.erpback.attendance.repository.AttendanceMapper;
import v1.erpback.department.domain.Department;
import v1.erpback.department.repository.DepartmentMapper;
import v1.erpback.noti.repository.NotiMapper;
import v1.erpback.user.domain.User;
import v1.erpback.user.repository.UserMapper;
import v1.erpback.util.PageResultDTO;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceMapper attendanceMapper;
    private final NotiMapper notiMapper;
    private final UserMapper userMapper;
    private final DepartmentMapper departmentMapper;

    public void save(AttendanceSaveDTO attendance, User user) {
        if (attendance.getReason().equals(Attendance.Reason.BUSINESS_TRIP) ||
                attendance.getReason().equals(Attendance.Reason.VACATION) ||
                attendance.getReason().equals(Attendance.Reason.WORKING_OUTSIDE)) {
            User hod = userMapper.findHod(attendance.getDepartmentId());
            String reason;
            switch (attendance.getReason()) {
                case BUSINESS_TRIP:
                    reason = "외근";
                    break;
                case VACATION:
                    reason = "휴가";
                    break;
                case WORKING_OUTSIDE:
                    reason = "출장";
                    break;
                default:
                    reason = "알 수 없음";
                    break;
            }
            String notiMessage = user.getName() + "님이 " + reason + "건에 대해 결제를 신청했습니다.";
            if (Objects.equals(hod.getId(), user.getId())) {
                Department department = departmentMapper.findById(user.getDepartmentId());
                System.out.println(department.getParentId());
                attendance.setDepartmentId(department.getParentId());
                User diffHod = userMapper.findHod(department.getParentId());
                notiMapper.save(diffHod.getId() , notiMessage);
            } else {
                notiMapper.save(hod.getId() , notiMessage);
            }

        }
        attendanceMapper.save(attendance, user.getId());
    }

    public PageResultDTO<AttendanceListDTO> list(User user, int page, int size, String search) {
        int offset = (page - 1) * size;
        List<AttendanceListDTO> content = attendanceMapper.attendanceList(user.getDepartmentId() , offset, size, search);
        long totalElements = attendanceMapper.attendanceListSize(user.getDepartmentId() , search);
        return new PageResultDTO<>(content, totalElements, page, size);
    }

    public void approve(String name, Long attendanceId) {

        Attendance attendance = attendanceMapper.find(attendanceId);
        String reason;
        switch (attendance.getReason()) {
            case BUSINESS_TRIP:
                reason = "외근";
                break;
            case VACATION:
                reason = "휴가";
                break;
            case WORKING_OUTSIDE:
                reason = "출장";
                break;
            default:
                reason = "알 수 없음";
                break;
        }
        String notiMessage = "신청하신 " + reason + "건이 승인되었습니다.";
        notiMapper.save(attendance.getUserId() , notiMessage);
        attendanceMapper.approve(attendanceId);
    }

    public void deny(String name, Long attendanceId) {
        Attendance attendance = attendanceMapper.find(attendanceId);
        String reason;
        switch (attendance.getReason()) {
            case BUSINESS_TRIP:
                reason = "외근";
                break;
            case VACATION:
                reason = "휴가";
                break;
            case WORKING_OUTSIDE:
                reason = "출장";
                break;
            default:
                reason = "알 수 없음";
                break;
        }
        String notiMessage = "신청하신 " + reason + "건이 반려되었습니다.";
        notiMapper.save(attendance.getUserId() , notiMessage);
        attendanceMapper.deny(attendanceId);
    }

    public PageResultDTO<AttendanceListDTO> waitList(User user, int page, int size, String search) {
        int offset = (page - 1) * size;
        List<AttendanceListDTO> content = attendanceMapper.waitList(user.getDepartmentId() , offset, size, search);
        long totalElements = attendanceMapper.waitListSize(user.getDepartmentId() , search);
        return new PageResultDTO<>(content, totalElements, page, size);
    }

    public PageResultDTO<AttendanceListDTO> myList(User user, int page, int size, String search) {
        int offset = (page - 1) * size;
        List<AttendanceListDTO> content = attendanceMapper.myList(user.getId() , offset, size, search);
        long totalElements = attendanceMapper.myListSize(user.getId() , search);
        return new PageResultDTO<>(content, totalElements, page, size);
    }

    public void leaveWork(Long attendanceId, String endDateTime, Attendance.Reason reason) {
        attendanceMapper.leaveWork(attendanceId, endDateTime, reason);
    }
}

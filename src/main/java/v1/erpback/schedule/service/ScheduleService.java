package v1.erpback.schedule.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import v1.erpback.department.domain.Department;
import v1.erpback.department.repository.DepartmentMapper;
import v1.erpback.department.service.DepartmentService;
import v1.erpback.file.domain.FileDomain;
import v1.erpback.file.repository.FileMapper;
import v1.erpback.schedule.domain.Schedule;
import v1.erpback.schedule.dto.ScheduleCreateRequest;
import v1.erpback.schedule.dto.ScheduleDetailDTO;
import v1.erpback.schedule.dto.ScheduleListDTO;
import v1.erpback.schedule.repository.ScheduleMapper;
import v1.erpback.user.domain.User;
import v1.erpback.user.dto.UserLoginListDTO;
import v1.erpback.user.repository.UserMapper;
import v1.erpback.util.PageResultDTO;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleMapper scheduleMapper;
    private final UserMapper userMapper;

    private static final String FILE_SAVE_PATH = "C:/Users/vlrma/boardfile/test/";
    private final FileMapper fileMapper;
    private final DepartmentService departmentService;

    public void create(Long id, ScheduleCreateRequest schedule) throws IOException {

        Schedule toSchedule = Schedule.builder()
                .userId(id)
                .startDateTime(schedule.getStartDateTime())
                .endDateTime(schedule.getEndDateTime())
                .title(schedule.getTitle())
                .place(schedule.getPlace())
                .content(schedule.getContent())
                .build();
        scheduleMapper.createSchedule(toSchedule);
        Long scheduleId = toSchedule.getId();

        Set<Long> userIds = new HashSet<>();

        scheduleMapper.createUser(scheduleId, id, "USER");
        for (Long departmentId : schedule.getDepartmentId()) {
            List<Department> allSubDepts = departmentService.getAllSubDepartments(departmentId, new HashSet<>());
            scheduleMapper.createDepartment(scheduleId, departmentId, "DEPARTMENT");
            for (Department department : allSubDepts) {
                List<User> users = userMapper.findByDepartmentId(department.getId());
                for (User user : users) {
                    scheduleMapper.createUser(scheduleId, user.getId(), "IN-USER");
                    userIds.add(user.getId());
                }
            }
        }
        for (Long userId : schedule.getUserId()) {
            if (!userIds.contains(userId)) {
                scheduleMapper.createUser(scheduleId, userId, "USER");
                userIds.add(userId);
            }
        }
        if (!userIds.contains(id)) {
            scheduleMapper.createUser(scheduleId, id, "USER");
        }
        //saveFiles(schedule.getFiles(), scheduleId);
    }

    public ScheduleDetailDTO detail(Long scheduleId) throws IOException {
        return scheduleMapper.detail(scheduleId);
    }


    private void saveFiles(List<MultipartFile> files, Long postMessageId) throws IOException {
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String originalFileName = file.getOriginalFilename();
                    String storedFileName = System.currentTimeMillis() + "_" + originalFileName;
                    String savePath = FILE_SAVE_PATH + storedFileName;

                    // Ensure directory exists
                    File directory = new File(FILE_SAVE_PATH);
                    if (!directory.exists()) {
                        directory.mkdirs();
                    }

                    // Save the file
                    file.transferTo(new File(savePath));

                    // Save metadata
                    FileDomain newFile = FileDomain.builder()
                            .scheduleId(postMessageId)
                            .originalFileName(originalFileName)
                            .storedFileName(storedFileName)
                            .build();
                    fileMapper.scheduleCreate(newFile);
                }
            }
        }
    }
    public PageResultDTO<ScheduleListDTO> list(Long userId,Long departmentId, int page, int size) {
        int offset = (page - 1) * size;
        List<ScheduleListDTO> content = scheduleMapper.list(userId, departmentId, offset, size);
        long totalElements = scheduleMapper.listSize(userId, departmentId);
        return new PageResultDTO<>(content, totalElements, page, size);
    }
}

package v1.erpback.config;

import jakarta.mail.MessagingException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import v1.erpback.attendance.domain.Attendance;
import v1.erpback.attendance.dto.AttendanceSaveDTO;
import v1.erpback.attendance.repository.AttendanceMapper;
import v1.erpback.email.service.EmailService;
import v1.erpback.postMessage.domain.PostMessage;
import v1.erpback.postMessage.dto.PostMessageDeleteListDTO;
import v1.erpback.postMessage.dto.PostMessageReceiver;
import v1.erpback.postMessage.repository.PostMessageMapper;
import v1.erpback.user.domain.User;
import v1.erpback.vote.domain.Vote;
import v1.erpback.vote.domain.VoteResult;
import v1.erpback.vote.repository.VoteMapper;
import v1.erpback.vote.repository.VoteResultMapper;
import v1.erpback.vote.repository.VoteUserMapper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Component
public class ScheduledTask {
    private final VoteMapper voteMapper;
    private final VoteResultMapper voteResultMapper;
    private final VoteUserMapper voteUserMapper;
    private final PostMessageMapper postMessageMapper;
    private final AttendanceMapper attendanceMapper;
    private final EmailService emailService;

    public ScheduledTask(VoteMapper voteMapper, VoteResultMapper voteResultMapper, VoteUserMapper voteUserMapper, PostMessageMapper postMessageMapper, AttendanceMapper attendanceMapper, EmailService emailService) {
        this.voteMapper = voteMapper;
        this.voteResultMapper = voteResultMapper;
        this.voteUserMapper = voteUserMapper;
        this.postMessageMapper = postMessageMapper;
        this.attendanceMapper = attendanceMapper;
        this.emailService = emailService;
    }

    @Scheduled(cron = "0 */30 * * * *", zone = "Asia/Seoul")
    public void runTask() {
        System.out.println("Task executed at: " + LocalDateTime.now(ZoneId.of("Asia/Seoul")));
        // 실행할 코드 작성

        List<Vote> voteList = voteMapper.timeoverList();
        for (Vote vote : voteList) {
            List<VoteResult> voteResultList = voteResultMapper.countAnswer(vote.getId());

            for (VoteResult voteResult : voteResultList) {
                Long countAnswerUser = voteUserMapper.countUser(vote.getId(), voteResult.getId());
                voteResultMapper.result(vote.getId(), countAnswerUser, voteResult.getId());
            }
            voteMapper.voteEnd(vote.getId());
        }
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void runTask2() {
        System.out.println("Task2 executed at: " + LocalDateTime.now());
        postMessageMapper.autoDelete();
        postMessageMapper.autoReceiverDelete();
        System.out.println("auto delete 30days after trash post message");
    }

    @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Seoul")
    public void runTask3() throws MessagingException {
        System.out.println("Task3 executed at: " + LocalDateTime.now());

        List<User> absentUsers = attendanceMapper.getAbsentUsers();

        for (User user : absentUsers) {
            String title = "지각 알림 메일";

            String content = "<html>"
                    + "<body>"
                    + "<p>9시가 넘어 "+ user.getName() +"님은 지각입니다.</p>"
                    + "<footer style='color: grey; font-size: small;'>"
                    + "<p>※본 메일은 자동응답 메일이므로 본 메일에 회신하지 마시기 바랍니다.</p>"
                    + "</footer>"
                    + "</body>"
                    + "</html>";
            emailService.sendEmail(user.getEmail(), title, content);
        }
        System.out.println("auto sending tardiness user mail");
    }

    @Scheduled(cron = "0 0 18 * * *", zone = "Asia/Seoul")
    public void runTask4() throws MessagingException {
        System.out.println("Task4 executed at: " + LocalDateTime.now());
        List<User> absentUsers = attendanceMapper.getAbsentUsers();


        for (User user : absentUsers) {
            String title = "결근 알림 메일";

            String content = "<html>"
                    + "<body>"
                    + "<p>오후 6시가 넘어 "+ user.getName() +"님은 결근입니다.</p>"
                    + "<footer style='color: grey; font-size: small;'>"
                    + "<p>※본 메일은 자동응답 메일이므로 본 메일에 회신하지 마시기 바랍니다.</p>"
                    + "</footer>"
                    + "</body>"
                    + "</html>";
            emailService.sendEmail(user.getEmail(), title, content);
            AttendanceSaveDTO attendanceSaveDTO = AttendanceSaveDTO.builder()
                    .startDateTime(LocalDateTime.now())
                    .endDateTime(LocalDateTime.now())
                    .reason(Attendance.Reason.ABSENTEEISM)
                    .status(Attendance.Status.NOT_APPLICABLE)
                    .departmentId(user.getDepartmentId())
                    .build();
            attendanceMapper.save(attendanceSaveDTO, user.getId());
        }

        System.out.println("auto create absenteeism user attendance");
    }
}

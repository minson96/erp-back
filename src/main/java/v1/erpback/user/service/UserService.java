package v1.erpback.user.service;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import v1.erpback.Exception.CustomException;
import v1.erpback.Exception.ErrorCode.ErrorCode;
import v1.erpback.auth.domain.Jwt;
import v1.erpback.auth.service.JwtTokenService;
import v1.erpback.department.domain.Department;
import v1.erpback.department.repository.DepartmentMapper;
import v1.erpback.department.service.DepartmentService;
import v1.erpback.email.VerificationCode;
import v1.erpback.email.repository.EmailMapper;
import v1.erpback.email.service.EmailService;
import v1.erpback.user.domain.User;
import v1.erpback.user.dto.*;
import v1.erpback.user.repository.LoginMapper;
import v1.erpback.user.repository.UserMapper;
import v1.erpback.util.PageResultDTO;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    private final EmailService emailService;

    private final EmailMapper emailMapper;

    private final JwtTokenService jwtTokenService;
    private final LoginMapper loginMapper;
    private final DepartmentMapper departmentMapper;
    private final DepartmentService departmentService;
    private final SimpMessagingTemplate messagingTemplate;


    public Jwt login(UserLoginDTO userLoginDTO) {
        User user = userMapper.findByAccount(userLoginDTO.getAccount());
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        if (!passwordEncoder.matches(userLoginDTO.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        return jwtTokenService.createTokens(user.getId(), user.getAccount());
    }

    public void signUp(UserSignUpDTO userSignUpDTO) throws CustomException, IOException {
        String fileName = null;
        String storedFileName = null;
        User existUser = userMapper.findByAccount(userSignUpDTO.getAccount());
        if (existUser != null) {
            throw new CustomException(ErrorCode.HAS_ACCOUNT);
        }
        if (!isValidPassword(userSignUpDTO.getPassword())) {
            throw new CustomException(ErrorCode.NOT_VALID_PASSWORD);
        }

        if (!userSignUpDTO.getProfileImg().isEmpty()) {
            MultipartFile file = userSignUpDTO.getProfileImg();
            fileName = file.getOriginalFilename();
            storedFileName = System.currentTimeMillis() + "_" + fileName;
            String savePath = "C:/Users/vlrma/boardfile/test/" + storedFileName;
            file.transferTo(new File(savePath));
        }
        User user = User.builder()
                .name(userSignUpDTO.getName())
                .account(userSignUpDTO.getAccount())
                .email(userSignUpDTO.getEmail())
                .password(passwordEncoder.encode(userSignUpDTO.getPassword()))
//                .originalFileName(fileName)
                .departmentId(userSignUpDTO.getDepartmentId())
                .positionId(userSignUpDTO.getPositionId())
//                .storedFileName(storedFileName)
                .status(User.SignUpStatus.PENDING_APPROVAL)
                .companyId(userSignUpDTO.getCompanyId())
                .role(User.Role.USER)
                .build();
        userMapper.signUp(user);
    }

    private boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        boolean hasUppercase = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLowercase = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.chars().anyMatch(ch -> "!@#$%^&*()-_+=<>?".indexOf(ch) >= 0);

        return hasUppercase && hasLowercase && hasDigit && hasSpecial;
    }

    public void sendCodeToEmail(String email) {
        VerificationCode createCode = createVerificationCode(email);
        String title = "Img Forest 이메일 인증 번호";

        String content = "<html>"
                + "<body>"
                + "<h1>GroupWare 인증 코드: " + createCode.getCodes() + "</h1>"
                + "<p>해당 코드를 홈페이지에 입력하세요.</p>"
                + "<footer style='color: grey; font-size: small;'>"
                + "<p>※본 메일은 자동응답 메일이므로 본 메일에 회신하지 마시기 바랍니다.</p>"
                + "</footer>"
                + "</body>"
                + "</html>";
        try {
            emailService.sendEmail(email, title, content);
        } catch (MessagingException e) {
            throw new RuntimeException("Unable to send email in sendCodeToEmail", e);
        }
    }

    private VerificationCode createVerificationCode(String email) {
        String randomCode = generateRandomCode(8);
        VerificationCode code = VerificationCode.builder()
                .email(email)
                .codes(randomCode)
                .expiresTime(LocalDateTime.now().plusDays(1))
                .build();
        emailMapper.save(code);
        return code;
    }

    public String generateRandomCode(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }

        return sb.toString();
    }

    public boolean verifyCode(String email, String code) {
        VerificationCode verificationCode = emailMapper.findByEmailAndCode(email, code);

        if (verificationCode != null) {
            return verificationCode.getExpiresTime().isAfter(LocalDateTime.now());
        } else {
            return false;
        }
    }

    @Transactional
    @Scheduled(cron = "0 0 12 * * ?") // 매일 정오에 해당 만료 코드 삭제
    public void deleteExpiredVerificationCodes() {
        emailMapper.deleteByExpiresTimeBefore(LocalDateTime.now());
    }

    public User findByAccount(String account) {
        return userMapper.findByAccount(account);
    }

    public boolean AccessUserCheck(User user) {
        if (user.getStatus() == User.SignUpStatus.PENDING_APPROVAL) {
            return false;
        } else {
            return true;
        }
    }

    public UserProfileResponseDTO userInfo(User user) {

        return userMapper.userInfo(user.getId());
    }

    public Jwt refreshToken(String refreshToken) {
        return jwtTokenService.refreshAccessToken(refreshToken);
    }

    public void approve(List<Long> ids) {
        for(Long id : ids) {
            System.out.println("=-======" + id + "=======");
            User user = userMapper.findByIdApprove(id);
            User updateUser = User.builder()
                    .id(user.getId())
                    .status(User.SignUpStatus.APPROVED)
                    .build();
            userMapper.approve(updateUser);
            sendApproveToEmail(user.getEmail());
        }

    }
    private void sendApproveToEmail(String email) {
        String title = "가입 승인 메일";

        String content = "<html>"
                + "<body>"
                + "<h1>가입이 승인되었습니다.</h1>"
                + "<p>이제 로그인이 가능합니다.</p>"
                + "<footer style='color: grey; font-size: small;'>"
                + "<p>※본 메일은 자동응답 메일이므로 본 메일에 회신하지 마시기 바랍니다.</p>"
                + "</footer>"
                + "</body>"
                + "</html>";
        try {
            emailService.sendEmail(email, title, content);
        } catch (MessagingException e) {
            throw new RuntimeException("Unable to send email in sendApproveToEmail", e);
        }
    }

    public User myInfo(String authorizationHeader) {
        Jwt jwt = jwtTokenService.getAccessTokenInfo(authorizationHeader);
        User user = userMapper.findById(jwt.getLoginId());

        return user;
    }

    public void update(UserUpdateDTO userUpdateDTO) throws IOException, CustomException {
        String fileName = null;
        String storedFileName = null;

        if (userUpdateDTO.getProfileImg() != null && !userUpdateDTO.getProfileImg().isEmpty()) {
            MultipartFile file = userUpdateDTO.getProfileImg();
            fileName = file.getOriginalFilename();
            storedFileName = System.currentTimeMillis() + "_" + fileName;
            String savePath = "C:/Users/vlrma/boardfile/test/" + storedFileName;
            file.transferTo(new File(savePath));
        }
        User getUser = userMapper.findById(userUpdateDTO.getId());
        User user = User.builder()
                .id(userUpdateDTO.getId())
                .email(userUpdateDTO.getEmail())
                .companyNumber(userUpdateDTO.getCompanyNumber() != null ? userUpdateDTO.getCompanyNumber() : getUser.getCompanyNumber())
                .phoneNumber(userUpdateDTO.getPhoneNumber() != null ? userUpdateDTO.getPhoneNumber() : getUser.getPhoneNumber())
                .originalFileName(userUpdateDTO.getProfileImg() != null ? fileName : getUser.getOriginalFileName())
                .departmentId(userUpdateDTO.getDepartmentId().orElse(0L))
                .positionId(userUpdateDTO.getPositionId().orElse(0L))
                .storedFileName(userUpdateDTO.getProfileImg() != null ? storedFileName : getUser.getStoredFileName())
                .build();
        userMapper.update(user);
    }

    public Long passwordReset(String name, String email, String newPassword) {
        User user = userMapper.findByEmail(email, name);
        User changeUser = User.builder()
                .id(user.getId())
                .password(passwordEncoder.encode(newPassword))
                .build();
        return userMapper.passwordReset(changeUser);
    }

    public User findByEmail(String email, String name) {
        return userMapper.findByEmail(email, name);
    }

    public User findByUser(String account, String email, String name) {
        return userMapper.findByUser(account, email, name);
    }

    public void loginLogging(Long id, String clientIp, String browser, String os) {
        loginMapper.save(id, clientIp, browser, os, LocalDateTime.now(ZoneId.of("Asia/Seoul")));
    }

    public void delete(List<Long> ids) {
        for(Long id : ids) {
            User user = userMapper.findById(id);
            userMapper.delete(user);
            //sendApproveToEmail(user.getEmail());
        }
    }

    public PageResultDTO<AdminsUserListDTO> adminsUserList(Long departmentId,int page, int size, String search) {
        int offset = (page - 1) * size;

        List<AdminsUserListDTO> content = userMapper.adminsDepartmentList(departmentId, offset, size, search);
        long totalElements = userMapper.countContent(departmentId, search);
        return new PageResultDTO<>(content, totalElements, page, size);
    }

    public PageResultDTO<UserLoginListDTO> loginList(Long id, int page, int size, String search) {
        int offset = (page - 1) * size;
        List<UserLoginListDTO> content = loginMapper.list(id, offset, size, search);
        long totalElements = loginMapper.loginSize(id, search);
        return new PageResultDTO<>(content, totalElements, page, size);
    }

    public List<UserSearchListDTO> searchList(Long companyId, Long id, String search) {
        return userMapper.searchlist(companyId, id, search);

    }

    public List<UserSearchListDTO> departInSearchList(Long departmentId, Long id, String search) {
        return userMapper.departInSearchlist(departmentId, id, search);
    }


//    public void deleteUseCode(String verificationCode) {
//        emailMapper.deleteUseCode(verificationCode);
//    }

//    public List<User> getUsersByDepartment() {
//        return userMapper.findUsersByDepartment(departmentId);
//    }
}

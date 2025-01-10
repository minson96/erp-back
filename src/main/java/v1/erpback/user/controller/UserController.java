package v1.erpback.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import v1.erpback.Exception.CustomException;
import v1.erpback.Exception.ErrorCode.ErrorCode;
import v1.erpback.auth.domain.Jwt;

import v1.erpback.department.service.DepartmentService;
import v1.erpback.user.domain.User;
import v1.erpback.user.dto.*;
import v1.erpback.user.repository.LoginMapper;
import v1.erpback.user.service.UserService;
import v1.erpback.util.PageResultDTO;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@Slf4j
public class UserController {
    private final UserService userService;
    private final DepartmentService departmentService;

    @PostMapping("/login")
    public ResponseEntity<Jwt> login(@RequestHeader(value = "User-Agent") String userAgent,
                                     @RequestHeader(value = "X-Forwarded-For", required = false) String xForwardedFor,
                                     @RequestBody UserLoginDTO userLoginDTO) throws CustomException
    {
        User user = userService.findByAccount(userLoginDTO.getAccount());
        if (!userService.AccessUserCheck(user)) {
            throw new CustomException(ErrorCode.NOT_ACCESS_USER);
        }
        Jwt jwt = userService.login(userLoginDTO);
        // IP 정보
        String clientIp = (xForwardedFor != null) ? xForwardedFor : "127.0.0.1";

        // 브라우저와 OS 정보 추출
        String browser = extractBrowserFromUserAgent(userAgent);
        String os = extractOsFromUserAgent(userAgent);

        userService.loginLogging(user.getId(),clientIp, browser, os);
        HttpHeaders headers = new HttpHeaders();
        headers.add("access-token", "Bearer " + jwt.getAccessToken());
        headers.add("refresh-token", jwt.getRefreshToken());
        headers.add("access-token-exp", jwt.getAccessTokenExp().toString());
        headers.add("refresh-token-exp", jwt.getRefreshTokenExp().toString());

        return ResponseEntity.ok().headers(headers).build();
    }
//    @GetMapping("/email")
//    public ResponseEntity<User> email(@RequestParam String email) {
//        return ResponseEntity.ok().body(userService.findByEmail(email));
//    }



    @PostMapping("/create")
    public ResponseEntity<String> signUp(@ModelAttribute UserSignUpDTO userSignUpDTO) throws CustomException, IOException {
        userService.signUp(userSignUpDTO);
        return ResponseEntity.ok("Sign up successful");
    }

    @PostMapping("/update")
    public ResponseEntity<String> update(@ModelAttribute UserUpdateDTO userUpdateDTO) throws CustomException, IOException  {
        System.out.println(userUpdateDTO.getAccount());
        userService.update(userUpdateDTO);
        return ResponseEntity.ok("Sign up successful");
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponseDTO> profile(@RequestHeader("Authorization") String authorizationHeader) {
        User user = userService.myInfo(authorizationHeader);

        return ResponseEntity.ok().header("Content-Type", "application/json; charset=UTF-8").body(userService.userInfo(user));
    }

    @GetMapping("/userInfo")
    public ResponseEntity<UserProfileResponseDTO> userInfo(@RequestParam String account) {
        User user = userService.findByAccount(account);

        return ResponseEntity.ok().header("Content-Type", "application/json; charset=UTF-8").body(userService.userInfo(user));
    }

    @GetMapping("/myInfo")
    public ResponseEntity<User> myInfo(@RequestHeader("Authorization") String authorizationHeader) {
        return ResponseEntity.ok().header("Content-Type", "application/json; charset=UTF-8").body(userService.myInfo(authorizationHeader));
    }
    @PostMapping("/refresh")
    public ResponseEntity<Jwt> refresh(String refreshToken){
        Jwt jwt = userService.refreshToken(refreshToken);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Token", "Bearer " + jwt.getAccessToken());
        headers.add("Access-Token-Exp", jwt.getAccessTokenExp().toString());
        return ResponseEntity.ok().headers(headers).build();
    }

    @PostMapping("/sendEmail")
    public ResponseEntity<String> sendEmail(@RequestBody UserEmailRequestDTO userEmailRequestDTO) {
        userService.sendCodeToEmail(userEmailRequestDTO.getEmail());
        return ResponseEntity.ok("이메일 전송 성공");
    }

    @PostMapping("/verifyEmail")
    public ResponseEntity<UserEmailVerifyResponseDTO> verifyEmail(@RequestBody UserEmailVerifyRequestDTO requestDto) {
        boolean isVerified = userService.verifyCode(requestDto.getEmail(), requestDto.getVerificationCode());
        UserEmailVerifyResponseDTO responseDto = new UserEmailVerifyResponseDTO();
        responseDto.setVerified(isVerified);
//        if (isVerified) {
//            userService.deleteUseCode(requestDto.getVerificationCode());
//        }
        responseDto.setMessage(isVerified ? "Email verified successfully." : "Invalid or expired verification code.");
        return ResponseEntity.ok().body(responseDto);
    }

    @GetMapping("/findAccount")
    public ResponseEntity<String> findAccount(@RequestParam String email , @RequestParam String name) {
        return ResponseEntity.ok().body(userService.findByEmail(email, name).getAccount());
    }
    @GetMapping("/findUser")
    public ResponseEntity<String> findUser(@RequestParam String account, @RequestParam String email , @RequestParam String name) {
        return ResponseEntity.ok().body(userService.findByUser(account, email, name).getAccount());
    }

    @PostMapping("/password-reset")
    public ResponseEntity<String> passwordReset(@RequestBody PasswordResetDTO resetDTO) {
        userService.passwordReset(resetDTO.getName(), resetDTO.getEmail(), resetDTO.getNewPassword());
        return ResponseEntity.ok("Password reset successful");
    }

    //admin controller

    @PostMapping("/approve")
    public ResponseEntity<String> approve(@RequestHeader("Authorization") String header, @RequestBody ApproveUserDTO  approveUserDTO) throws CustomException {

        userService.approve(approveUserDTO.getIds());
        return ResponseEntity.ok().body("Approve successful");
    }

    @PostMapping("/delete")
    public ResponseEntity<String> delete(@RequestHeader("Authorization") String header, @RequestBody DeleteUserDTO  deleteUserDTO) throws CustomException {
        User user = userService.myInfo(header);
        userService.delete(deleteUserDTO.getIds());
        return ResponseEntity.ok().body("Approve successful");
    }

    @GetMapping("/adminsList")
    public ResponseEntity<PageResultDTO<AdminsUserListDTO>> adminsUserList(@RequestHeader("Authorization") String header,
                                                                           @RequestParam Long departmentId,
                                                                           @RequestParam(defaultValue = "1") int page,
                                                                           @RequestParam(defaultValue = "10") int size,
                                                                           @RequestParam(defaultValue = "") String search) throws CustomException {
        User user = userService.myInfo(header);
        if (departmentId == null) {
            departmentId = departmentService.findRootDepartment(user.getCompanyId());
        }
        return ResponseEntity.ok().body(userService.adminsUserList(departmentId, page, size, search));
    }
    @GetMapping("/loginList")
    public ResponseEntity<PageResultDTO<UserLoginListDTO>> userLoginList(@RequestHeader("Authorization") String header,
                                                                         @RequestParam(defaultValue = "1") int page,
                                                                         @RequestParam(defaultValue = "10") int size,
                                                                         @RequestParam(defaultValue = "") String search) throws CustomException {

        User user = userService.myInfo(header);
        return ResponseEntity.ok().body(userService.loginList(user.getId(), page, size, search));
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserSearchListDTO>> search(@RequestHeader("Authorization") String header,
                                         @RequestParam Long companyId,
                                         @RequestParam(defaultValue = "") String search) throws CustomException {
        User user = userService.myInfo(header);
        return ResponseEntity.ok().body(userService.searchList(companyId, user.getId(), search));
    }

    @GetMapping("/departinsearch")
    public ResponseEntity<List<UserSearchListDTO>> departinsearch(@RequestHeader("Authorization") String header,
                                                          @RequestParam Long departmentId,
                                                          @RequestParam(defaultValue = "") String search) throws CustomException {
        User user = userService.myInfo(header);
        return ResponseEntity.ok().body(userService.departInSearchList(departmentId, user.getId(), search));
    }


    //private method

    private String extractBrowserFromUserAgent(String userAgent) {
        if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
            return "Internet Explorer";
        } else if (userAgent.contains("Edge")) {
            return "Microsoft Edge";
        } else if (userAgent.contains("Chrome")) {
            return "Google Chrome";
        } else if (userAgent.contains("Safari")) {
            return "Safari";
        } else if (userAgent.contains("Firefox")) {
            return "Mozilla Firefox";
        } else {
            return "Other";
        }
    }

    private String extractOsFromUserAgent(String userAgent) {
        if (userAgent.contains("Windows")) {
            return "Windows";
        } else if (userAgent.contains("Mac")) {
            return "MacOS";
        } else if (userAgent.contains("Android")) {
            return "Android";
        } else if (userAgent.contains("Linux")) {
            return "Linux";
        } else {
            return "Unknown";
        }
    }

//    @GetMapping("/department")
//    public ResponseEntity<List<User>> getUsersByDepartment(@RequestParam Long departmentId) {
//        List<User> users = userService.getUsersByDepartment(departmentId);
//        return ResponseEntity.ok(users);
//    }
}

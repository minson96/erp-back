package v1.erpback.Exception.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    HAS_COMPANY(HttpStatus.BAD_REQUEST, "COMPANY-001", "이미 생성된 회사입니다."),
    HAS_ACCOUNT(HttpStatus.BAD_REQUEST, "ACCOUNT-001", "존재하는 아이디입니다."),
    NOT_VALID_PASSWORD(HttpStatus.BAD_REQUEST, "ACCOUNT-002", "비밀번호 양식에 맞지 않습니다."),
    EMPTY_DATA(HttpStatus.BAD_REQUEST, "DEPARTMENT-001", "부모 객체가 비어있습니다."),
    NOT_ACCESS_USER(HttpStatus.BAD_REQUEST, "ACCOUNT-002", "아직 승인되지 않은 유저입니다."),
    NOT_AUTHORITY_USER(HttpStatus.BAD_REQUEST, "ACCOUNT-003", "권한이 없습니다."),
    ALREADY_VOTED(HttpStatus.BAD_REQUEST, "VOTE-001", "이미 투표를 진행하였습니다.");
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}

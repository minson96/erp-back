package v1.erpback.Exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import v1.erpback.Exception.ErrorCode.ErrorCode;

@Getter
@AllArgsConstructor
public class CustomException extends Exception{
    ErrorCode errorCode;
}

package uoscs.capstone.allyojo.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    // 에러이름(에러코드, 에러메시지)

    // Common
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error occurred."),

    // jwt
    JWT_SIGNATURE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "토큰 서명 중 오류가 발생했습니다.");

    private final Integer status;
    private final String message;
}

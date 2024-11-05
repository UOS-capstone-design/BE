package uoscs.capstone.allyojo.exception.global;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    // 에러이름(에러코드, 에러메시지)
    // Common
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error occurred."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "해당 아이디의 유저가 없습니다."),

    // Guardian
    GUARDIAN_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "해당 아이디의 보호자가 없습니다."),

    // Mission
    MISSION_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "해당 아이디의 미션이 없습니다."),

    // Alarm
    ALARM_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "해당 아이디의 알람이 없습니다."),

    // Todo
    TODO_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "해당 아이디의 투두가 없습니다."),

    // jwt
    JWT_SIGNATURE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "토큰 서명 중 오류가 발생했습니다.");

    private final Integer status;
    private final String message;
}

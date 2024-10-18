package uoscs.capstone.allyojo.exception;

import lombok.Getter;

@Getter
public class ErrorResponse {
    private final Integer status;
    private final String message;

    public ErrorResponse(ErrorCode errorCode) {
        this.status = errorCode.getStatus();
        this.message = errorCode.getMessage();
    }
}

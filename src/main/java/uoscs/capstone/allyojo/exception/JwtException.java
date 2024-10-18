package uoscs.capstone.allyojo.exception;

import lombok.Getter;

@Getter
public class JwtException extends RuntimeException {

    private final ErrorCode errorCode;
    public JwtException(final ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}

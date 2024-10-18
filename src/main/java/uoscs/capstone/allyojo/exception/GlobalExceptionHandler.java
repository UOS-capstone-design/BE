package uoscs.capstone.allyojo.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        log.error("handleBusinessException", e);
        ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());
        return new ResponseEntity<>(
                errorResponse, HttpStatusCode.valueOf(e.getErrorCode().getStatus())
        );
    }

    protected ResponseEntity<ErrorResponse> handleJwtException(JwtException e) {
        log.error("handleJwtException", e);
        ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());
        return new ResponseEntity<>(
                errorResponse, HttpStatusCode.valueOf(e.getErrorCode().getStatus())
        );
    }
}

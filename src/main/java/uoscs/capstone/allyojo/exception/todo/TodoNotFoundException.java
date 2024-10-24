package uoscs.capstone.allyojo.exception.todo;

import lombok.Getter;
import uoscs.capstone.allyojo.exception.global.BusinessException;
import uoscs.capstone.allyojo.exception.global.ErrorCode;

@Getter
public class TodoNotFoundException extends BusinessException {
    public TodoNotFoundException() {
        super(ErrorCode.TODO_NOT_FOUND);
    }
}

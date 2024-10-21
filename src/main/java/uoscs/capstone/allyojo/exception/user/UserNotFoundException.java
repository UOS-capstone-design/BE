package uoscs.capstone.allyojo.exception.user;

import lombok.Getter;
import uoscs.capstone.allyojo.exception.global.BusinessException;
import uoscs.capstone.allyojo.exception.global.ErrorCode;

@Getter
public class UserNotFoundException extends BusinessException {

    public UserNotFoundException() {
        super(ErrorCode.User_NOT_FOUND);
    }
}

package uoscs.capstone.allyojo.exception.guardian;

import lombok.Getter;
import uoscs.capstone.allyojo.exception.global.BusinessException;
import uoscs.capstone.allyojo.exception.global.ErrorCode;

@Getter
public class UserNotManagedException extends BusinessException {
    public UserNotManagedException() {
        super(ErrorCode.USER_NOT_MANAGED);
    }
}

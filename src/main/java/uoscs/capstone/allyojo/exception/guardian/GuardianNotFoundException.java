package uoscs.capstone.allyojo.exception.guardian;

import lombok.Getter;
import uoscs.capstone.allyojo.exception.global.BusinessException;
import uoscs.capstone.allyojo.exception.global.ErrorCode;

@Getter
public class GuardianNotFoundException extends BusinessException {
    public GuardianNotFoundException() {
        super(ErrorCode.GUARDIAN_NOT_FOUND);
    }
}

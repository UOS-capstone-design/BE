package uoscs.capstone.allyojo.exception.guardian;

import uoscs.capstone.allyojo.exception.global.BusinessException;
import uoscs.capstone.allyojo.exception.global.ErrorCode;

public class GuardianPhoneNumberDuplicatedException extends BusinessException {
    public GuardianPhoneNumberDuplicatedException() {
        super(ErrorCode.GUARDIAN_PHONENUMBER_DUPLICATED);}
}

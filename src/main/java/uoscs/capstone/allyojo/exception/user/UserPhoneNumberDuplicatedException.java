package uoscs.capstone.allyojo.exception.user;

import uoscs.capstone.allyojo.exception.global.BusinessException;
import uoscs.capstone.allyojo.exception.global.ErrorCode;

public class UserPhoneNumberDuplicatedException extends BusinessException {
    public UserPhoneNumberDuplicatedException() {
        super(ErrorCode.USER_PHONENUMBER_DUPLICATED);}
}

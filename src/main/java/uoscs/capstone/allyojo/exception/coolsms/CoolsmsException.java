package uoscs.capstone.allyojo.exception.coolsms;

import uoscs.capstone.allyojo.exception.global.BusinessException;
import uoscs.capstone.allyojo.exception.global.ErrorCode;

public class CoolsmsException extends BusinessException {
    public CoolsmsException() { super(ErrorCode.COOLSMS_ERROR);}

}

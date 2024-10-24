package uoscs.capstone.allyojo.exception.alarm;

import lombok.Getter;
import uoscs.capstone.allyojo.exception.global.BusinessException;
import uoscs.capstone.allyojo.exception.global.ErrorCode;

@Getter
public class AlarmNotFoundException extends BusinessException {
    public AlarmNotFoundException() {
        super(ErrorCode.ALARM_NOT_FOUND);
    }
}

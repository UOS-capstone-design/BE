package uoscs.capstone.allyojo.exception.mission;

import lombok.Getter;
import uoscs.capstone.allyojo.exception.global.BusinessException;
import uoscs.capstone.allyojo.exception.global.ErrorCode;

@Getter
public class MissionNotFoundException extends BusinessException {

    public MissionNotFoundException() {
        super(ErrorCode.MISSION_NOT_FOUND);
    }
}

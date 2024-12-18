package uoscs.capstone.allyojo.dto.guardian.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import uoscs.capstone.allyojo.entity.Mission;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor
public class UpdateUsersAlarmRequestDTO {
    private final Long alarmId;
    private final String missionName;
    private final LocalDateTime alarmTime;
    private final String title;
    private final String active;
    private final Integer alarmDays;
    private final Integer delayTimes;
    private final String restrictAlarm;
    private final String isVibration;
    private final Integer volume;
    private final Integer alarmInterval;
    private final Boolean disabled;
}

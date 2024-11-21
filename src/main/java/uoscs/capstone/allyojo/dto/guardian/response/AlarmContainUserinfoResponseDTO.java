package uoscs.capstone.allyojo.dto.guardian.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uoscs.capstone.allyojo.entity.Alarm;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlarmContainUserinfoResponseDTO {
    private Long alarmId;
    private LocalDateTime alarmTime;
    private String username;
    private String phoneNumber;
    private String name;
    private String missionName;
    private Boolean active;
    private Integer alarmDays;
    private Integer delayTimes;
    private Boolean restrictAlarm;
    private Boolean isVibration;
    private Integer volume;
    private Integer alarmInterval;
    private Boolean createdByGuardian;

    // 엔티티로부터 ResponseDTO 생성
    public static AlarmContainUserinfoResponseDTO fromAlarm(Alarm alarm) {
        return AlarmContainUserinfoResponseDTO.builder()
                .alarmId(alarm.getAlarmId())
                .alarmTime(alarm.getAlarmTime())
                .username(alarm.getUser().getUsername())
                .phoneNumber(alarm.getUser().getPhoneNumber())
                .name(alarm.getUser().getName())
                .missionName(alarm.getMission().getMissionName())
                .active(alarm.getActive())
                .alarmDays(alarm.getAlarmDays())
                .delayTimes(alarm.getDelayTimes())
                .restrictAlarm(alarm.getRestrictAlarm())
                .isVibration(alarm.getIsVibration())
                .volume(alarm.getVolume())
                .alarmInterval(alarm.getAlarmInterval())
                .createdByGuardian(alarm.getCreatedByGuardian())
                .build();
    }
}

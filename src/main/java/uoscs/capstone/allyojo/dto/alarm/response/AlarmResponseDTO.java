package uoscs.capstone.allyojo.dto.alarm.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uoscs.capstone.allyojo.entity.Alarm;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlarmResponseDTO {
    private Long alarmId;
    private LocalDateTime alarmTime;
    private String title;
    private String missionName;
    private Boolean active;
    private Integer alarmDays;
    private Integer delayTimes;
    private Boolean restrictAlarm;
    private Boolean isVibration;
    private Integer volume;
    private Integer alarmInterval;
    private Boolean createdByGuardian;
    private Boolean disabled;

    // 엔티티로부터 ResponseDTO 생성
    public static AlarmResponseDTO fromAlarm(Alarm alarm) {
        return AlarmResponseDTO.builder()
                .alarmId(alarm.getAlarmId())
                .alarmTime(alarm.getAlarmTime())
                .title(alarm.getTitle())
                .missionName(alarm.getMission().getMissionName())
                .active(alarm.getActive())
                .alarmDays(alarm.getAlarmDays())
                .delayTimes(alarm.getDelayTimes())
                .restrictAlarm(alarm.getRestrictAlarm())
                .isVibration(alarm.getIsVibration())
                .volume(alarm.getVolume())
                .alarmInterval(alarm.getAlarmInterval())
                .createdByGuardian(alarm.getCreatedByGuardian())
                .disabled(alarm.getDisabled())
                .build();
    }
}

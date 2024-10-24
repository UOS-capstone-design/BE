package uoscs.capstone.allyojo.dto.alarm.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uoscs.capstone.allyojo.entity.Alarm;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlarmResponseDTO {
    private Long alarmId;
    private String title;
    private LocalTime alarmTime;
    private Boolean active;
    private Integer alarmDays;
    private Integer delayTimes;
    private Boolean restrictAlarm;
    private Boolean isVibration;
    private Integer volume;
    private Integer alarmInterval;

    // 엔티티로부터 ResponseDTO 생성
    public static AlarmResponseDTO fromAlarm(Alarm alarm) {
        return AlarmResponseDTO.builder()
                .alarmId(alarm.getAlarmId())
                .title(alarm.getTitle())
                .alarmTime(alarm.getAlarmTime())
                .active(alarm.getActive())
                .alarmDays(alarm.getAlarmDays())
                .delayTimes(alarm.getDelayTimes())
                .restrictAlarm(alarm.getRestrictAlarm())
                .isVibration(alarm.getIsVibration())
                .volume(alarm.getVolume())
                .alarmInterval(alarm.getAlarmInterval())
                .build();
    }
}

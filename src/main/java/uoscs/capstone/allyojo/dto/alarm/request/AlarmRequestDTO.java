package uoscs.capstone.allyojo.dto.alarm.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlarmRequestDTO {
    private Long alarmId;
    private String username; // findUserIdByUsername
    private String missionName; // findMissionIdByMissionName
    private LocalDateTime alarmTime;
    private String title;
    private String active; // 알람 활성 여부, "true / false"
    private Integer alarmDays; // 요일
    private Integer delayTimes;
    private String restrictAlarm; // 엄격 여부. "true / false", parseBoolean으로 변환
    private String isVibration; // 위와 마찬가지
    private Integer volume;
    private Integer alarmInterval;
    @Schema(example = "false")
    private Boolean disabled;
}



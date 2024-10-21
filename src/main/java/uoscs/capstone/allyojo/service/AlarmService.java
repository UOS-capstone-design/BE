package uoscs.capstone.allyojo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uoscs.capstone.allyojo.dto.alarm.request.AddAlarmRequestDTO;
import uoscs.capstone.allyojo.dto.alarm.response.AlarmListResponseDTO;
import uoscs.capstone.allyojo.entity.Alarm;
import uoscs.capstone.allyojo.entity.Mission;
import uoscs.capstone.allyojo.entity.User;
import uoscs.capstone.allyojo.exception.mission.MissionNotFoundException;
import uoscs.capstone.allyojo.exception.user.UserNotFoundException;
import uoscs.capstone.allyojo.repository.AlarmRepository;
import uoscs.capstone.allyojo.repository.MissionRepository;
import uoscs.capstone.allyojo.repository.UserRepository;

import static java.lang.Boolean.parseBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AlarmService {
    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;
    private final MissionRepository missionRepository;

    // 알람 등록
    public Alarm addAlarm(AddAlarmRequestDTO dto) {

        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new UserNotFoundException());
        Mission mission = missionRepository.findByMissionName(dto.getMissionName())
                .orElseThrow(() -> new MissionNotFoundException());

        Alarm alarm = Alarm.builder()
                .alarmId(dto.getAlarmId())
                .user(user)
                .mission(mission)
                .title(dto.getTitle())
                .alarmTime(dto.getTimer().toLocalTime())
                .active(parseBoolean(dto.getActive()))
                .alarmDays(dto.getAlarmDays())
                .delayTimes(dto.getDelayTimes())
                .restrictAlarm(parseBoolean(dto.getRestrictAlarm()))
                .isVibration(parseBoolean(dto.getIsVibration()))
                .volume(dto.getVolume())
                .alarmInterval(dto.getAlarmInterval())
                .build();

        return alarmRepository.save(alarm);
    }
    // 알람 조회
    public Alarm findAllByUserId(Long userId) {
//        return alarmRepository.findAllByUserUserId(userId).stream()
//                .map(Alarm)
        return null;
    }

    // 알람 수정
    public Alarm updateAlarm(AddAlarmRequestDTO addAlarmRequestDTO) {
        return null;
    }

    // 알람 삭제
    public Alarm deleteAlarm(Long alarmId) {
        return null;
    }
}

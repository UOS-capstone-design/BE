package uoscs.capstone.allyojo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uoscs.capstone.allyojo.dto.alarm.request.AlarmRequestDTO;
import uoscs.capstone.allyojo.entity.Alarm;
import uoscs.capstone.allyojo.entity.Mission;
import uoscs.capstone.allyojo.entity.User;
import uoscs.capstone.allyojo.exception.alarm.AlarmNotFoundException;
import uoscs.capstone.allyojo.exception.mission.MissionNotFoundException;
import uoscs.capstone.allyojo.exception.user.UserNotFoundException;
import uoscs.capstone.allyojo.repository.AlarmRepository;
import uoscs.capstone.allyojo.repository.MissionRepository;
import uoscs.capstone.allyojo.repository.UserRepository;

import java.util.List;

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
    public Alarm addAlarm(AlarmRequestDTO dto) {

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
    public List<Alarm> findAllByUserId(Long userId) {
          return alarmRepository.findAllByUserUserId(userId); // 빈 리스트 반환 가능.
    }

    // 알람 수정
    public Alarm updateAlarm(AlarmRequestDTO dto) {
        Alarm alarm = alarmRepository.findById(dto.getAlarmId())
                .orElseThrow(() -> new AlarmNotFoundException());
        // 알람 정보 업데이트
        // 알람 아이디, 유저, 미션은 그대로
        alarm = Alarm.builder()
                .alarmId(alarm.getAlarmId())
                .user(alarm.getUser()) // 기존 유저 유지
                .mission(alarm.getMission()) // 기존 미션 유지
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

    // 알람 삭제
    public void deleteAlarm(Long alarmId) {
        Alarm alarm = alarmRepository.findById(alarmId)
                .orElseThrow(() -> new AlarmNotFoundException());
        alarmRepository.delete(alarm);
    }
}

package uoscs.capstone.allyojo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import uoscs.capstone.allyojo.dto.alarm.request.AlarmRequestDTO;
import uoscs.capstone.allyojo.dto.guardian.request.AddUserToGuardianRequestDTO;
import uoscs.capstone.allyojo.dto.guardian.request.GuardianJoinRequestDTO;
import uoscs.capstone.allyojo.dto.guardian.request.UpdateUsersAlarmRequestDTO;
import uoscs.capstone.allyojo.entity.*;
import uoscs.capstone.allyojo.exception.alarm.AlarmNotFoundException;
import uoscs.capstone.allyojo.exception.guardian.GuardianNotFoundException;
import uoscs.capstone.allyojo.exception.guardian.UserNotManagedException;
import uoscs.capstone.allyojo.exception.mission.MissionNotFoundException;
import uoscs.capstone.allyojo.exception.user.UserNotFoundException;
import uoscs.capstone.allyojo.repository.AlarmRepository;
import uoscs.capstone.allyojo.repository.GuardianRepository;
import uoscs.capstone.allyojo.repository.MissionRepository;
import uoscs.capstone.allyojo.repository.UserRepository;

import java.util.List;

import static java.lang.Boolean.parseBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class GuardianService {
    private final GuardianRepository guardianRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;
    private final MissionRepository missionRepository;
    private final AlarmRepository alarmRepository;
    private final UserService userService;

    // 보호자 회원가입
    public Guardian joinGuardian(GuardianJoinRequestDTO guardianJoinRequestDTO) {
        String encodedPassword = bCryptPasswordEncoder.encode(guardianJoinRequestDTO.getPassword());

        Guardian guardian =
                Guardian.builder()
                    .guardianName(guardianJoinRequestDTO.getGuardianName())
                    .password(encodedPassword)
                    .name(guardianJoinRequestDTO.getName())
                    .phoneNumber(guardianJoinRequestDTO.getPhoneNumber())
                    .build();

        // 노인 아이디를 입력했다면
        if (guardianJoinRequestDTO.getSeniorName() != null) {
            // 노인 사용자 찾기
            User user = userRepository.findByUsername(guardianJoinRequestDTO.getSeniorName())
                    .orElseThrow(UserNotFoundException::new);

            // 기존 User 엔티티 복사 및 Guardian 관계 설정
            User updatedUser = User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .name(user.getName())
                    .userGrade(user.getUserGrade())
                    .phoneNumber(user.getPhoneNumber())
                    .guardian(guardian)  // 관계 설정
                    .build();

            userRepository.save(updatedUser);
            guardian.addManagedUser(updatedUser);
        }

        return guardianRepository.save(guardian);

    }

    // 보호자가 유저를 추가
    public User addUserToGuardian(AddUserToGuardianRequestDTO dto) {
        String guardianName = dto.getGuardianName();
        String userPhoneNumber = dto.getUserPhoneNumber();

        Guardian guardian = guardianRepository.findByGuardianName(guardianName)
                .orElseThrow(GuardianNotFoundException::new);
        User user = userRepository.findByPhoneNumber(userPhoneNumber)
                .orElseThrow(UserNotFoundException::new);

        user.addGuardian(guardian);

        return userRepository.save(user);
    }

    // 보호자가 관리하는 노인 리스트 조회
    public List<User> getManagedUsers(String guardianName) { // guardianName은 로그인할 때 쓰는 아이디
        Guardian guardian = guardianRepository.findByGuardianName(guardianName)
                .orElseThrow(GuardianNotFoundException::new);

        return guardian.getUsers();
    }

    // 보호자가 노인의 알람을 추가
    public Alarm addAlarmForUser(String guardianName, String username, AlarmRequestDTO dto) {
        Guardian guardian = guardianRepository.findByGuardianName(guardianName)
                .orElseThrow(GuardianNotFoundException::new);
        User user = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);
        Mission mission = missionRepository.findByMissionName(dto.getMissionName())
                .orElseThrow(MissionNotFoundException::new);

        // 보호자가 해당 노인을 관리하는지 확인
        if (!guardian.getUsers().contains(user)) {
            throw new UserNotManagedException();
        }

        Alarm alarm = Alarm.builder()
                .alarmId(dto.getAlarmId())
                .user(user)
                .mission(mission)
                .alarmTime(dto.getAlarmTime())
                .active(parseBoolean(dto.getActive()))
                .alarmDays(dto.getAlarmDays())
                .delayTimes(dto.getDelayTimes())
                .restrictAlarm(parseBoolean(dto.getRestrictAlarm()))
                .isVibration(parseBoolean(dto.getIsVibration()))
                .volume(dto.getVolume())
                .alarmInterval(dto.getAlarmInterval())
                .createdByGuardian(true)
                .build();

        return alarmRepository.save(alarm);
    }

    // 보호자가 관리하는 노인의 알람 조회
    public List<Alarm> getAlarmsForUser(String guardianName, String username) {
        Guardian guardian = guardianRepository.findByGuardianName(guardianName)
                .orElseThrow(GuardianNotFoundException::new);
        User user = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);
        // 보호자가 해당 노인을 관리하는지 확인
        if (!guardian.getUsers().contains(user)) {
            throw new UserNotManagedException();
        }

        return alarmRepository.findAllByUserUsernameAndCreatedByGuardian(username);
    }

    // 보호자가 관리하는 노인의 알람 수정
    public Alarm updateAlarmForUser(String guardianName, String username, UpdateUsersAlarmRequestDTO dto) {
        Guardian guardian = guardianRepository.findByGuardianName(guardianName)
                .orElseThrow(GuardianNotFoundException::new);
        User user = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);
        Mission mission = missionRepository.findByMissionName(dto.getMissionName())
                .orElseThrow(MissionNotFoundException::new);

        // 보호자가 해당 노인을 관리하는지 확인
        if (!guardian.getUsers().contains(user)) {
            throw new UserNotManagedException();
        }

        Alarm alarm = alarmRepository.findById(dto.getAlarmId())
                .orElseThrow(AlarmNotFoundException::new);

        alarm.update(
                mission,
                dto.getAlarmTime(),
                Boolean.parseBoolean(dto.getActive()),
                dto.getAlarmDays(),
                dto.getDelayTimes(),
                Boolean.parseBoolean(dto.getRestrictAlarm()),
                Boolean.parseBoolean(dto.getIsVibration()),
                dto.getVolume(),
                dto.getAlarmInterval(),
                true
        );

        return alarmRepository.save(alarm);
    }

    public void deleteAlarmForUser(String guardianName, String username, Long alarmId) {
        Guardian guardian = guardianRepository.findByGuardianName(guardianName)
                .orElseThrow(GuardianNotFoundException::new);
        User user = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        // 보호자가 해당 노인을 관리하는지 확인
        if (!guardian.getUsers().contains(user)) {
            throw new UserNotManagedException();
        }
        alarmRepository.deleteById(alarmId);
    }
}

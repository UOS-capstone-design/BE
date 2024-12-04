package uoscs.capstone.allyojo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import uoscs.capstone.allyojo.dto.alarm.request.AlarmRequestDTO;
import uoscs.capstone.allyojo.dto.guardian.request.*;
import uoscs.capstone.allyojo.dto.guardian.response.FindAllUsersResponseDTO;
import uoscs.capstone.allyojo.dto.nutrient.request.FoodReportRequestDTO;
import uoscs.capstone.allyojo.dto.nutrient.response.FoodReportResponseDTO;
import uoscs.capstone.allyojo.dto.verification.request.ReportRequestDTO;
import uoscs.capstone.allyojo.dto.verification.response.BloodPressureReportResponseDTO;
import uoscs.capstone.allyojo.dto.verification.response.ReportResponseDTO;
import uoscs.capstone.allyojo.entity.*;
import uoscs.capstone.allyojo.exception.alarm.AlarmNotFoundException;
import uoscs.capstone.allyojo.exception.guardian.GuardianNotFoundException;
import uoscs.capstone.allyojo.exception.guardian.UserNotManagedException;
import uoscs.capstone.allyojo.exception.mission.MissionNotFoundException;
import uoscs.capstone.allyojo.exception.user.UserNotFoundException;
import uoscs.capstone.allyojo.repository.*;

import java.util.ArrayList;
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
    private final VerificationService verificationService;
    private final UserService userService;
    private final NutrientService nutrientService;
    private final VerificationRepository verificationRepository;

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

    // guardianName 중복체크
    public boolean isGuardianNameDuplicate(String guardianName) {
        return guardianRepository.existsByGuardianName(guardianName);
    }

    // 보호자 정보 조회
    public Guardian getGuardianInfo(String guardianName) {
        return guardianRepository.findByGuardianName(guardianName)
                .orElseThrow(GuardianNotFoundException::new);
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
    public List<FindAllUsersResponseDTO> getManagedUsers(String guardianName) { // guardianName은 로그인할 때 쓰는 아이디
        Guardian guardian = guardianRepository.findByGuardianName(guardianName)
                .orElseThrow(GuardianNotFoundException::new);

        List<User> users = guardian.getUsers();
        List<FindAllUsersResponseDTO> findAllUsersResponseDTOS = new ArrayList<>();
        for (User user : users) {
            List<String> missions = verificationRepository.findDistinctMissionNamesByUsername(user.getUsername());
            FindAllUsersResponseDTO findAllUsersResponseDTO = FindAllUsersResponseDTO.builder()
                    .username(user.getUsername())
                    .name(user.getName())
                    .phoneNumber(user.getPhoneNumber())
                    .age(user.getAge())
                    .gender(user.getGender().name())
                    .missions(missions)
                    .build();
            findAllUsersResponseDTOS.add(findAllUsersResponseDTO);
        }
        return findAllUsersResponseDTOS;
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
                .title(dto.getTitle())
                .active(parseBoolean(dto.getActive()))
                .alarmDays(dto.getAlarmDays())
                .delayTimes(dto.getDelayTimes())
                .restrictAlarm(parseBoolean(dto.getRestrictAlarm()))
                .isVibration(parseBoolean(dto.getIsVibration()))
                .volume(dto.getVolume())
                .alarmInterval(dto.getAlarmInterval())
                .createdByGuardian(true)
                .disabled(false)
                .build();

        return alarmRepository.save(alarm);
    }

    // 보호자가 관리하는 모든 노인의 알람 조회
    public List<Alarm> getAlarmsByGuardian(String guardianName) {
        return alarmRepository.findAllByGuardianName(guardianName);
    }

    // 보호자가 관리하는 노인의 알람 조회(보호자이름, 유저이름)
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
                dto.getTitle(),
                Boolean.parseBoolean(dto.getActive()),
                dto.getAlarmDays(),
                dto.getDelayTimes(),
                Boolean.parseBoolean(dto.getRestrictAlarm()),
                Boolean.parseBoolean(dto.getIsVibration()),
                dto.getVolume(),
                dto.getAlarmInterval(),
                true,
                dto.getDisabled()
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

    // 보호자 유저 리포트 조회 (혈당, 복약)
    public ReportResponseDTO getReport(GuardianReportRequestDTO dto) {
        Guardian guardian = guardianRepository.findByGuardianName(dto.getGuardianName())
                .orElseThrow(GuardianNotFoundException::new);
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(UserNotFoundException::new);

        // 보호자가 해당 노인을 관리하는지 확인
        if (!guardian.getUsers().contains(user)) {
            throw new UserNotManagedException();
        }

        ReportRequestDTO reportRequestDTO = ReportRequestDTO.builder()
                .username(dto.getUsername())
                .missionName(dto.getMissionName())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .build();

        return verificationService.getReport(reportRequestDTO);
    }

    // 보호자 식사 리포트 조회
    public BloodPressureReportResponseDTO getBloodPressureReport(GuardianReportRequestDTO dto) {
        Guardian guardian = guardianRepository.findByGuardianName(dto.getGuardianName())
                .orElseThrow(GuardianNotFoundException::new);
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(UserNotFoundException::new);

        // 보호자가 해당 노인을 관리하는지 확인
        if (!guardian.getUsers().contains(user)) {
            throw new UserNotManagedException();
        }

        ReportRequestDTO reportRequestDTO = ReportRequestDTO.builder()
                .username(dto.getUsername())
                .missionName(dto.getMissionName())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .build();

        return verificationService.getBloodPressureReport(reportRequestDTO);
    }


    // 보호자 혈압 리포트 조회
    public FoodReportResponseDTO getFoodReport(GuardianFoodReportRequestDTO dto) {
        Guardian guardian = guardianRepository.findByGuardianName(dto.getGuardianName())
                .orElseThrow(GuardianNotFoundException::new);
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(UserNotFoundException::new);

        // 보호자가 해당 노인을 관리하는지 확인
        if (!guardian.getUsers().contains(user)) {
            throw new UserNotManagedException();
        }

        FoodReportRequestDTO foodReportRequestDTO = FoodReportRequestDTO.builder()
                .username(dto.getUsername())
                .reportDate(dto.getReportDate())
                .build();

        return nutrientService.getFoodReport(foodReportRequestDTO);
    }
}

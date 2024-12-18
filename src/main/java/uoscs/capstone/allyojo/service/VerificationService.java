package uoscs.capstone.allyojo.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uoscs.capstone.allyojo.dto.verification.request.ReportRequestDTO;
import uoscs.capstone.allyojo.dto.verification.request.VerificationRequestDTO;
import uoscs.capstone.allyojo.dto.verification.response.BloodPressureReportResponseDTO;
import uoscs.capstone.allyojo.dto.verification.response.ReportResponseDTO;
import uoscs.capstone.allyojo.dto.verification.response.VerificationResponseDTO;
import uoscs.capstone.allyojo.entity.Alarm;
import uoscs.capstone.allyojo.entity.Mission;
import uoscs.capstone.allyojo.entity.User;
import uoscs.capstone.allyojo.entity.Verification;
import uoscs.capstone.allyojo.exception.alarm.AlarmNotFoundException;
import uoscs.capstone.allyojo.exception.mission.MissionNotFoundException;
import uoscs.capstone.allyojo.exception.user.UserNotFoundException;
import uoscs.capstone.allyojo.repository.AlarmRepository;
import uoscs.capstone.allyojo.repository.MissionRepository;
import uoscs.capstone.allyojo.repository.UserRepository;
import uoscs.capstone.allyojo.repository.VerificationRepository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class VerificationService {
    private final VerificationRepository verificationRepository;
    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;
    private final MissionRepository missionRepository;

    public Verification addVerification(VerificationRequestDTO dto) {
        Alarm alarm = alarmRepository.findById(dto.getAlarmId())
                .orElseThrow(AlarmNotFoundException::new);

        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(UserNotFoundException::new);

        Verification verification = Verification.builder()
                .alarm(alarm)
                .user(user)
                .verificationDateTime(dto.getVerificationDateTime())
                .value(Optional.ofNullable(dto.getValue()).orElse(0.0)) // value 값이 0인 경우(혈압, 혈당이 아닌 경우) 0.0으로 저장.
                .value2(Optional.ofNullable(dto.getValue2()).orElse(0.0)) // 혈압인 경우 value2에 수축기 혈압 저장
                .result(dto.getResult())
                .build();

        return verificationRepository.save(verification);
    }

    // 리포트 조회 (혈당, 복약)
    public ReportResponseDTO getReport(ReportRequestDTO dto) {
//        LocalDateTime startDateTime = startDate.atStartOfDay();
//        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
//
//        return verificationRepository.findAllBetweenDate(startDateTime, endDateTime);

        String username = dto.getUsername();
        String missionName = dto.getMissionName();
        LocalDateTime startDate = dto.getStartDate().atStartOfDay();
        LocalDateTime endDate = dto.getEndDate().atTime(LocalTime.MAX);


        // 새로 추가한 부분: 알람을 가져와 성공률의 분모 구하기
        List<Alarm> alarms = alarmRepository.findAllByUserUsernameAndMissionMissionName(username, missionName);
        Mission mission = missionRepository.findByMissionName(missionName)
                .orElseThrow(MissionNotFoundException::new);

        List<Verification> verifications = verificationRepository
                .findByUserUsernameAndAlarmMissionMissionIdAndVerificationDateTimeBetween(
                        username,
                        mission.getMissionId(),
                        startDate,
                        endDate
                );
        // success ratio v1
//
//        int totalTriggered = 0;
//        for (Alarm alarm : alarms) {
//            totalTriggered += calculateTriggeredAlarmCount(alarm, startDate, endDate);
//        }
//
//        // 분자 = verification rows
//        int successfulVerifications = verifications.size();
//        double successRatio = 0.0;
//
//        if (successfulVerifications > 0) {
//            successRatio = (double) successfulVerifications / (double) totalTriggered;
//        }

        //int denominator = verifications.size();
        int denominator = countFalseVerification(username, mission.getMissionId(), startDate, endDate);
        int numerator = countTrueVerification(username, mission.getMissionId(), startDate, endDate);
        double successRatio = (double) numerator / (double) denominator;

        // 평균 계산
        Double averageValue = null;
        if (dto.getMissionName().equals("Manage blood sugar")) {
            OptionalDouble average = verifications.stream()
                    .filter(v -> v.getValue() != null)
                    .mapToDouble(Verification::getValue)
                    .average();
            averageValue = average.isPresent() ? average.getAsDouble() : 0.0;
        }

        List<VerificationResponseDTO> verificationDTOs = verifications.stream()
                .map(VerificationResponseDTO::fromVerification)
                .toList();

        return new ReportResponseDTO(verificationDTOs, averageValue, successRatio);
    }



    // 리포트 조회 (혈압)
    public BloodPressureReportResponseDTO getBloodPressureReport(ReportRequestDTO dto) {

        String username = dto.getUsername();
        String missionName = dto.getMissionName();
        LocalDateTime startDate = dto.getStartDate().atStartOfDay();
        LocalDateTime endDate = dto.getEndDate().atTime(LocalTime.MAX);

        Mission mission = missionRepository.findByMissionName(missionName)
                .orElseThrow(MissionNotFoundException::new);

        List<Verification> verifications = verificationRepository
                .findByUserUsernameAndAlarmMissionMissionIdAndVerificationDateTimeBetween(
                        username,
                        mission.getMissionId(),
                        startDate,
                        endDate
                );

        int denominator = countFalseVerification(username, mission.getMissionId(), startDate, endDate);
        int numerator = countTrueVerification(username, mission.getMissionId(), startDate, endDate);
        double successRatio = (double) numerator / (double) denominator;

        // 평균 계산
        Double lowAverageValue = null;
        Double highAverageValue = null;
        if (dto.getMissionName().equals("Manage blood pressure")) {
            OptionalDouble highValue = verifications.stream()
                    .filter(v -> v.getValue() != null)
                    .mapToDouble(Verification::getValue)
                    .average();
            highAverageValue = highValue.isPresent() ? highValue.getAsDouble() : 0.0;

            OptionalDouble lowValue = verifications.stream()
                    .filter(v -> v.getValue() != null)
                    .mapToDouble(Verification::getValue2)
                    .average();
            lowAverageValue = lowValue.isPresent() ? lowValue.getAsDouble() : 0.0;
        }

        String bloodPressureResult = classifyBloodPressure(lowAverageValue, highAverageValue);

        List<VerificationResponseDTO> verificationDTOs = verifications.stream()
                .map(VerificationResponseDTO::fromVerification)
                .toList();

        return new BloodPressureReportResponseDTO(verificationDTOs, lowAverageValue, highAverageValue, bloodPressureResult, successRatio);
    }

    // startDate, endDate 사이에 그 알람이 몇 번 발생해야 하는지 계산하는 메서드
    private int calculateTriggeredAlarmCount(Alarm alarm, LocalDateTime startDate, LocalDateTime endDate) {
        int count = 0;
        LocalDateTime current = startDate;
        log.info("현재 설정된 알람의 요일: {}", alarm.getAlarmDaysAsBinary());

        while (current.isBefore(endDate) || current.isEqual(endDate)) {
            if (alarm.isAlarmSetForDay((current.getDayOfWeek().getValue() - 1))) {
                count++;
            }
            current = current.plusDays(1);
        }
        return count;
    }


    public List<String> getDistinctMissionNamesByUsername(String username) {
        log.info("username = {}", username);
        return verificationRepository.findDistinctMissionNamesByUsername(username);
    }

    public int countTrueVerification(String username, Long missionId, LocalDateTime startDate, LocalDateTime endDate) {
        return verificationRepository.countTrueByUserAndAlarmMissionMissionId(username, missionId, startDate, endDate);
    }

    public int countFalseVerification(String username, Long missionId, LocalDateTime startDate, LocalDateTime endDate) {
        return verificationRepository.countFalseByUserAndAlarmMissionMissionId(username, missionId, startDate, endDate);
    }

    private String classifyBloodPressure(Double low, Double high) {
        if (low == null || high == null) {
            return "비정상 혈압 데이터 포함";
        }
        if (high < 120 && low < 80) {
            return "정상 혈압";
        } else if (high >= 140 && low < 90) {
            return "수축기 단독 고혈압";
        } else if (high >= 120 && high < 130 && low < 80) {
            return "주의 혈압";
        } else if ((high >= 130 && high < 140) || (low >= 80 && low < 90)) {
            return "고혈압 전단계";
        } else if ((high >= 140 && high < 160) || (low >= 90 && low < 100)) {
            return "고혈압 1기";
        } else if (high >= 160 || low >= 100) {
            return "고혈압 2기";
        } else {
            return "예외처리";
        }
    }
}

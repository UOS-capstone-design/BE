package uoscs.capstone.allyojo.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uoscs.capstone.allyojo.dto.verification.request.ReportRequestDTO;
import uoscs.capstone.allyojo.dto.verification.request.VerificationRequestDTO;
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
                .build();

        return verificationRepository.save(verification);
    }

    // 리포트 조회
    public ReportResponseDTO getReport(ReportRequestDTO dto) {
//        LocalDateTime startDateTime = startDate.atStartOfDay();
//        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
//
//        return verificationRepository.findAllBetweenDate(startDateTime, endDateTime);

        Mission mission = missionRepository.findByMissionName(dto.getMissionName())
                .orElseThrow(MissionNotFoundException::new);

        List<Verification> verifications = verificationRepository
                .findByUserUsernameAndAlarmMissionMissionIdAndVerificationDateTimeBetween(
                        dto.getUsername(),
                        mission.getMissionId(),
                        dto.getStartDate().atStartOfDay(),
                        dto.getEndDate().atTime(LocalTime.MAX)
                );

        // 평균 계산
        Double averageValue = null;
        if (dto.getMissionName().equals("혈압 측정") || dto.getMissionName().equals("혈당 측정")) {
            OptionalDouble average = verifications.stream()
                    .filter(v -> v.getValue() != null)
                    .mapToDouble(Verification::getValue)
                    .average();
            averageValue = average.isPresent() ? average.getAsDouble() : 0.0;
        }

        List<VerificationResponseDTO> verificationDTOs = verifications.stream()
                .map(VerificationResponseDTO::fromVerification)
                .toList();

        return new ReportResponseDTO(verificationDTOs, averageValue);
    }

    public List<String> getDistinctMissionNamesByUsername(String username) {
        log.info("username = {}", username);
        return verificationRepository.findDistinctMissionNamesByUsername(username);
    }
}

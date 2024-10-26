package uoscs.capstone.allyojo.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uoscs.capstone.allyojo.dto.verification.request.VerificationRequestDTO;
import uoscs.capstone.allyojo.entity.Alarm;
import uoscs.capstone.allyojo.entity.User;
import uoscs.capstone.allyojo.entity.Verification;
import uoscs.capstone.allyojo.exception.alarm.AlarmNotFoundException;
import uoscs.capstone.allyojo.exception.user.UserNotFoundException;
import uoscs.capstone.allyojo.repository.AlarmRepository;
import uoscs.capstone.allyojo.repository.UserRepository;
import uoscs.capstone.allyojo.repository.VerificationRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class VerificationService {
    private final VerificationRepository verificationRepository;
    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;

    public Verification addVerification(VerificationRequestDTO dto) {
        Alarm alarm = alarmRepository.findById(dto.getAlarmId())
                .orElseThrow(AlarmNotFoundException::new);

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(UserNotFoundException::new);

        Verification verification = Verification.builder()
                .verificationId(dto.getVerificationId())
                .alarm(alarm)
                .user(user)
                .verificationDateTime(dto.getVerificationDateTime())
                .result(dto.getResult())
                .value(Optional.ofNullable(dto.getValue()).orElse(0.0)) // value 값이 0인 경우(혈압, 혈당이 아닌 경우) 0.0으로 저장.
                .build();

        return verificationRepository.save(verification);
    }

    // 리포트 조회
    public List<Verification> getVerificationsBetweenDates(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        return verificationRepository.findAllBetweenDate(startDateTime, endDateTime);

    }
}

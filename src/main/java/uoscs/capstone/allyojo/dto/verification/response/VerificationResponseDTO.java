package uoscs.capstone.allyojo.dto.verification.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uoscs.capstone.allyojo.entity.Verification;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationResponseDTO {
    private Long verificationId;
    private Long alarmId;
    private Long userId;
    private LocalDateTime verificationDateTime;
    private String result;
    private Double value;

    public static VerificationResponseDTO fromVerification(Verification verification) {
        return VerificationResponseDTO.builder()
                .verificationId(verification.getVerificationId())
                .alarmId(verification.getAlarm().getAlarmId())
                .userId(verification.getUser().getUserId())
                .verificationDateTime(verification.getVerificationDateTime())
                .result(verification.getResult())
                .value(verification.getValue())
                .build();
    }
}

package uoscs.capstone.allyojo.dto.verification.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationRequestDTO {
    private Long verificationId;
    private Long alarmId;
    private Long userId;
    private LocalDateTime verificationDateTime;
    private String result;
    private Double value;
}

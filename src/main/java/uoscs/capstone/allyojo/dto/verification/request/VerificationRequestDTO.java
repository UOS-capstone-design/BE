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
    private Long alarmId;
    private String username;
    private LocalDateTime verificationDateTime;
    private Double value;
    private Double value2;
    private Boolean result;
}

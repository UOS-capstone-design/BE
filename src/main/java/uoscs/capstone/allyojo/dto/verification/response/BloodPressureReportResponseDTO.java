package uoscs.capstone.allyojo.dto.verification.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BloodPressureReportResponseDTO {
    private List<VerificationResponseDTO> verifications;
    private Double lowAverageValue; // 이완기
    private Double highAverageValue; // 수축기
    private String BloodPressureResult; // 혈압 분류 결과
    private double successRatio;
}

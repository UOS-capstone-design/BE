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
public class ReportResponseDTO {
    private List<VerificationResponseDTO> verifications;
    private Double averageValue; // 기간 내 혈압/혈당의 평균값
    private double successRatio;
}

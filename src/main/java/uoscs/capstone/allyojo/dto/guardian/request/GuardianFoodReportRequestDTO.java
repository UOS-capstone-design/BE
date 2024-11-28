package uoscs.capstone.allyojo.dto.guardian.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuardianFoodReportRequestDTO {
    private String guardianName;
    private String username;
    private LocalDateTime reportDate;
}

package uoscs.capstone.allyojo.dto.nutrient.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NutrientVerificationRequestDTO {
    private Long alarmId;
    private String username;
    private LocalDateTime verificationDateTime;
    private Boolean result;
    private List<FoodDTO> foods;
}

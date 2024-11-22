package uoscs.capstone.allyojo.dto.nutrient.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uoscs.capstone.allyojo.dto.nutrient.request.FoodDTO;
import uoscs.capstone.allyojo.dto.verification.response.VerificationResponseDTO;
import uoscs.capstone.allyojo.entity.Nutrient;
import uoscs.capstone.allyojo.entity.Verification;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NutrientVerificationResponseDTO {
    private VerificationResponseDTO verificationResponseDTO;
    private List<FoodResponseDTO> foods;
}

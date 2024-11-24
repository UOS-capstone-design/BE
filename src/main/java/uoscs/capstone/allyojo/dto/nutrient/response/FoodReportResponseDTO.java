package uoscs.capstone.allyojo.dto.nutrient.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uoscs.capstone.allyojo.dto.nutrient.request.FoodDTO;
import uoscs.capstone.allyojo.dto.verification.response.VerificationResponseDTO;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodReportResponseDTO {
    //    private VerificationResponseDTO verificationResponseDTO;
    //    private List<FoodDTO> foodDTOS;
    private List<NutrientVerificationResponseDTO> nutrientVerificationResponseDTOS;
    private List<FoodPercentResponseDTO> foodAverages;
}

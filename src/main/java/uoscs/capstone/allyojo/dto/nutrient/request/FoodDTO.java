package uoscs.capstone.allyojo.dto.nutrient.request;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodDTO {

   // private Long verificationId;
    private String foodName;
    private Double carbohydrates;
    private Double protein;
    private Double fat;
    private Double sodium;
}

package uoscs.capstone.allyojo.dto.nutrient.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uoscs.capstone.allyojo.entity.Nutrient;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodResponseDTO {

    private String foodName;
    private Double carbohydrates;
    private Double protein;
    private Double fat;
    private Double sodium;

    public static FoodResponseDTO fromNutrient(Nutrient nutrient) {
        return FoodResponseDTO.builder()
                .foodName(nutrient.getId().getFoodName())
                .carbohydrates(nutrient.getCarbohydrates())
                .protein(nutrient.getProtein())
                .fat(nutrient.getFat())
                .sodium(nutrient.getSodium())
                .build();
    }
}


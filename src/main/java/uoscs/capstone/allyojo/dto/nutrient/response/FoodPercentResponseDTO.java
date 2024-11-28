package uoscs.capstone.allyojo.dto.nutrient.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uoscs.capstone.allyojo.entity.Nutrient;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodPercentResponseDTO {

    private Double carbohydrates_percent;
    private Double protein_percent;
    private Double fat_percent;
    private Double sodium_percent;


    public static FoodPercentResponseDTO makePercent(List<Double> percentList) {
        return FoodPercentResponseDTO.builder()
                .carbohydrates_percent(percentList.get(0))
                .protein_percent(percentList.get(1))
                .fat_percent(percentList.get(2))
                .sodium_percent(percentList.get(3))
                .build();
    }
}


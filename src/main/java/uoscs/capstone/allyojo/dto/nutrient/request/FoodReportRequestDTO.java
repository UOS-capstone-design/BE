package uoscs.capstone.allyojo.dto.nutrient.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodReportRequestDTO {
    private String username;
    private LocalDateTime reportDate;
}

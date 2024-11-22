package uoscs.capstone.allyojo.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class NutritionStandard {

    private final String gender;
    private final double ageLimit; // 기준 나이
    private final double carbohydrate; // 탄수화물
    private final double protein; // 단백질
    private final double fat; // 지방
    private final double sodiumMin; // 나트륨 최소값
    private final double sodiumMax; // 나트륨 최대값
}


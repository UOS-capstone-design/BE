package uoscs.capstone.allyojo.data;

import uoscs.capstone.allyojo.entity.Gender;

import java.util.ArrayList;
import java.util.List;

public class NutritionStandardService {
    private final List<NutritionStandard> standards = new ArrayList<>();
    // 초기화: 기준 데이터 저장
    public NutritionStandardService() {
        standards.add(new NutritionStandard(Gender.MALE.name(), 50, 130, 65, 67, 1500, 2300));
        standards.add(new NutritionStandard(Gender.MALE.name(), 65, 130, 60, 59, 1500, 2300));
        standards.add(new NutritionStandard(Gender.MALE.name(), 75, 130, 60, 54, 1300, 2100));
        standards.add(new NutritionStandard(Gender.MALE.name(), Integer.MAX_VALUE, 130, 60, 51, 1100, 1700));

        standards.add(new NutritionStandard(Gender.FEMALE.name(), 50, 130, 50, 51, 1500, 2300));
        standards.add(new NutritionStandard(Gender.FEMALE.name(), 65, 130, 50, 45, 1500, 2300));
        standards.add(new NutritionStandard(Gender.FEMALE.name(), 75, 130, 50, 43, 1300, 2100));
        standards.add(new NutritionStandard(Gender.FEMALE.name(), Integer.MAX_VALUE, 130, 50, 40, 1100, 1700));
    }
    // 조건에 따른 기준 조회
    public NutritionStandard getStandard(String gender, Integer age) {
        return standards.stream()
                .filter(s -> s.getGender().equals(gender) && age <= s.getAgeLimit())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("기준 데이터를 찾을 수 없습니다."));
    }
}
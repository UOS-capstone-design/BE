package uoscs.capstone.allyojo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uoscs.capstone.allyojo.data.NutritionStandard;
import uoscs.capstone.allyojo.data.NutritionStandardService;
import uoscs.capstone.allyojo.dto.nutrient.request.FoodDTO;
import uoscs.capstone.allyojo.dto.nutrient.request.FoodReportRequestDTO;
import uoscs.capstone.allyojo.dto.nutrient.request.NutrientVerificationRequestDTO;
import uoscs.capstone.allyojo.dto.nutrient.response.FoodPercentResponseDTO;
import uoscs.capstone.allyojo.dto.nutrient.response.FoodReportResponseDTO;
import uoscs.capstone.allyojo.dto.nutrient.response.FoodResponseDTO;
import uoscs.capstone.allyojo.dto.nutrient.response.NutrientVerificationResponseDTO;
import uoscs.capstone.allyojo.dto.verification.response.VerificationResponseDTO;
import uoscs.capstone.allyojo.entity.*;
import uoscs.capstone.allyojo.exception.alarm.AlarmNotFoundException;
import uoscs.capstone.allyojo.exception.user.UserNotFoundException;
import uoscs.capstone.allyojo.repository.AlarmRepository;
import uoscs.capstone.allyojo.repository.NutrientRepository;
import uoscs.capstone.allyojo.repository.UserRepository;
import uoscs.capstone.allyojo.repository.VerificationRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NutrientService {
    private final UserRepository userRepository;
    private final VerificationRepository verificationRepository;
    private final AlarmRepository alarmRepository;
    private final NutrientRepository nutrientRepository;

    // 식사 관리 미션 검증 저장
    public NutrientVerificationResponseDTO addFoodVerification(NutrientVerificationRequestDTO dto) {
        Alarm alarm = alarmRepository.findById(dto.getAlarmId())
                .orElseThrow(AlarmNotFoundException::new);

        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(UserNotFoundException::new);

        Verification verification = Verification.builder()
                .alarm(alarm)
                .user(user)
                .verificationDateTime(dto.getVerificationDateTime())
                .value(0.0)
                .result(dto.getResult())
                .build();

        Verification savedVerification = verificationRepository.save(verification);
        VerificationResponseDTO verificationResponseDTO = VerificationResponseDTO.fromVerification(savedVerification);

        List<Nutrient> nutrients = dto.getFoods().stream()
                .map(foodDTO -> Nutrient.builder()
                        .verificationId(savedVerification.getVerificationId())
                        .foodName(foodDTO.getFoodName())
                        .verification(savedVerification)
                        .carbohydrates(foodDTO.getCarbohydrates())
                        .protein(foodDTO.getProtein())
                        .fat(foodDTO.getFat())
                        .sodium(foodDTO.getSodium())
                        .build())
                .toList();

        List<Nutrient> savedNutrients = nutrientRepository.saveAll(nutrients);
        List<FoodResponseDTO> foodResponseDTOS = savedNutrients.stream()
                .map(FoodResponseDTO::fromNutrient)
                .toList();
        return new NutrientVerificationResponseDTO(verificationResponseDTO, foodResponseDTOS);
    }

    // 식사 관리 리포트 리턴
    public FoodReportResponseDTO getFoodReport(FoodReportRequestDTO dto) {

        // 유저 정보 조회
        NutritionStandardService nutritionStandardService = new NutritionStandardService();
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(UserNotFoundException::new);
        int age = user.getAge();
        Gender gender = user.getGender();

        // 기준 영양정보 가져옴
        NutritionStandard standard = nutritionStandardService.getStandard(gender.name(), age);
        log.info("기준: 탄 = {}, 단 = {}, 지 = {}, 나트륨 = {}", standard.getCarbohydrate(), standard.getProtein(), standard.getFat(), standard.getSodiumMax());

        LocalDateTime startDate = dto.getReportDate().toLocalDate().atStartOfDay();
        LocalDateTime endDate = startDate.plusDays(1).minusSeconds(1);

        // 식사 관리 verification 조회
        List<Verification> verifications = verificationRepository.
                findByUserUsernameAndAlarmMissionMissionIdAndVerificationDateTimeBetween(
                        dto.getUsername(),
                        2L,
                        startDate,
                        endDate
                );

        // foodDTOs, nutrientTotals 초기화
        List<NutrientVerificationResponseDTO> nutrientVerificationResponseDTOS = new ArrayList<>();
        Map<String, Double> nutrientTotals = new HashMap<>();
        nutrientTotals.put("carbohydrates", 0.0);
        nutrientTotals.put("protein", 0.0);
        nutrientTotals.put("fat", 0.0);
        nutrientTotals.put("sodium", 0.0);

        for (Verification verification : verifications) {
            // verification과 연관되어 있는 nutrient 모두 가져옴.
            List<Nutrient> nutrients = nutrientRepository.findAllByVerification(verification);

            // FoodResponseDTO 생성
            List<FoodResponseDTO> foodResponses = nutrients.stream()
                    .map(nutrient -> FoodResponseDTO.builder()
                            .foodName(nutrient.getId().getFoodName())
                            .carbohydrates(nutrient.getCarbohydrates())
                            .protein(nutrient.getProtein())
                            .fat(nutrient.getFat())
                            .sodium(nutrient.getSodium())
                            .build())
                    .toList();

            // nutrients 갱신
            for (Nutrient nutrient : nutrients) {
                nutrientTotals.put("carbohydrates", nutrientTotals.get("carbohydrates") + nutrient.getCarbohydrates());
                nutrientTotals.put("protein", nutrientTotals.get("protein") + nutrient.getProtein());
                nutrientTotals.put("fat", nutrientTotals.get("fat") + nutrient.getFat());
                nutrientTotals.put("sodium", nutrientTotals.get("sodium") + nutrient.getSodium());
            }

            nutrientVerificationResponseDTOS.add(NutrientVerificationResponseDTO.builder()
                    .verificationResponseDTO(VerificationResponseDTO.fromVerification(verification))
                    .foods(foodResponses)
                    .build());
        }

        List<Double> percentList = List.of(
                nutrientTotals.get("carbohydrates") / standard.getCarbohydrate(),
                nutrientTotals.get("protein") / standard.getProtein(),
                nutrientTotals.get("fat") / standard.getFat(),
                nutrientTotals.get("sodium") / 2000D
        );

        FoodPercentResponseDTO foodPercentResponseDTO = FoodPercentResponseDTO.makePercent(percentList);

        return FoodReportResponseDTO.builder()
                .nutrientVerificationResponseDTOS(nutrientVerificationResponseDTOS)
                .foodAverages(List.of(foodPercentResponseDTO))
                .build();
    }
}

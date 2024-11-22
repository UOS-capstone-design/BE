package uoscs.capstone.allyojo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uoscs.capstone.allyojo.dto.nutrient.request.NutrientVerificationRequestDTO;
import uoscs.capstone.allyojo.dto.nutrient.response.FoodResponseDTO;
import uoscs.capstone.allyojo.dto.nutrient.response.NutrientVerificationResponseDTO;
import uoscs.capstone.allyojo.dto.verification.response.VerificationResponseDTO;
import uoscs.capstone.allyojo.entity.Alarm;
import uoscs.capstone.allyojo.entity.Nutrient;
import uoscs.capstone.allyojo.entity.User;
import uoscs.capstone.allyojo.entity.Verification;
import uoscs.capstone.allyojo.exception.alarm.AlarmNotFoundException;
import uoscs.capstone.allyojo.exception.user.UserNotFoundException;
import uoscs.capstone.allyojo.repository.AlarmRepository;
import uoscs.capstone.allyojo.repository.NutrientRepository;
import uoscs.capstone.allyojo.repository.UserRepository;
import uoscs.capstone.allyojo.repository.VerificationRepository;

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
}

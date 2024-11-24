package uoscs.capstone.allyojo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uoscs.capstone.allyojo.dto.nutrient.request.FoodReportRequestDTO;
import uoscs.capstone.allyojo.dto.nutrient.request.NutrientVerificationRequestDTO;
import uoscs.capstone.allyojo.dto.nutrient.response.FoodReportResponseDTO;
import uoscs.capstone.allyojo.dto.nutrient.response.NutrientVerificationResponseDTO;
import uoscs.capstone.allyojo.service.NutrientService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/food")
@Tag(name = "식사 관리")
public class NutrientController {
    private final NutrientService nutrientService;

    @PostMapping("/add")
    @Operation(summary = "식사 관리 검증 저장", description = "식사 미션에 대한 검증 항목을 저장합니다.")
    public ResponseEntity<NutrientVerificationResponseDTO> addMealVerification(@RequestBody NutrientVerificationRequestDTO dto) {
        NutrientVerificationResponseDTO responseDTO = nutrientService.addFoodVerification(dto);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/report")
    @Operation(summary = "식사 관리 리포트 조회", description = "일단위로 식사 관리 리포트를 조회합니다.")
    public ResponseEntity<FoodReportResponseDTO> getFoodReport(@RequestBody FoodReportRequestDTO dto) {
        FoodReportResponseDTO responseDTO = nutrientService.getFoodReport(dto);
        return ResponseEntity.ok(responseDTO);
    }
}

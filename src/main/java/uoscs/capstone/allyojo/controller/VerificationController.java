package uoscs.capstone.allyojo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uoscs.capstone.allyojo.dto.verification.request.ReportRequestDTO;
import uoscs.capstone.allyojo.dto.verification.request.VerificationRequestDTO;
import uoscs.capstone.allyojo.dto.verification.response.BloodPressureReportResponseDTO;
import uoscs.capstone.allyojo.dto.verification.response.ReportResponseDTO;
import uoscs.capstone.allyojo.dto.verification.response.VerificationResponseDTO;
import uoscs.capstone.allyojo.entity.Verification;
import uoscs.capstone.allyojo.service.VerificationService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/verification")
@Tag(name = "리포트")
public class VerificationController {
    private final VerificationService verificationService;

    @PostMapping("/add")
    @Operation(summary = "검증항목 저장", description = "verification 결과 저장")
    public ResponseEntity<VerificationResponseDTO> addVerification(@Valid @RequestBody VerificationRequestDTO verificationRequestDTO) {
        Verification verification = verificationService.addVerification(verificationRequestDTO);
        VerificationResponseDTO verificationResponseDTO = VerificationResponseDTO.fromVerification(verification);
        return ResponseEntity.ok(verificationResponseDTO);
    }

    // 수행한 적 있는 미션 이름 조회
    @GetMapping("/reports/{username}")
    @Operation(summary = "미션 이름 조회", description = "유저 이름을 받아 그 유저가 수행한 적이 있는 미션 이름을 모아 제공합니다.")
    public ResponseEntity<List<String>> getMissionNameByUsername(@PathVariable("username") String username) {
        log.info("controller username = {}", username);
        List<String> missionNames = verificationService.getDistinctMissionNamesByUsername(username);
        return ResponseEntity.ok(missionNames);
    }

    // 리포트 조회
    @PostMapping("/report")
    @Operation(summary = "리포트 조회", description = "주어진 날짜 사이에 포함된 검증 기록을 모아 제공합니다. (리포트 범위)")
    public ResponseEntity<ReportResponseDTO> getReport(@Valid @RequestBody ReportRequestDTO reportRequestDTO) {
        ReportResponseDTO report = verificationService.getReport(reportRequestDTO);
        return ResponseEntity.ok(report);
    }

    // 혈압 리포트 조회
    @PostMapping("/report/bloodPressure")
    @Operation(summary = "혈압 리포트 조회", description = "주어진 날짜 사이에 포함된 혈압 리포트를 제공합니다.")
    public ResponseEntity<BloodPressureReportResponseDTO> getBloodPressureReport(@RequestBody ReportRequestDTO reportRequestDTO) {
        BloodPressureReportResponseDTO report = verificationService.getBloodPressureReport(reportRequestDTO);
        return ResponseEntity.ok(report);
    }
}

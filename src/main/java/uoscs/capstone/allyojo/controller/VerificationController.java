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
import uoscs.capstone.allyojo.dto.verification.response.VerificationResponseDTO;
import uoscs.capstone.allyojo.entity.Verification;
import uoscs.capstone.allyojo.service.VerificationService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/report")
@Tag(name = "리포트")
public class VerificationController {
    private final VerificationService verificationService;

    @PostMapping("/add")
    @Operation(summary = "리포트 추가", description = "리포트 항목을 추가합니다: verification 결과 저장")
    public ResponseEntity<VerificationResponseDTO> addVerification(@Valid @RequestBody VerificationRequestDTO verificationRequestDTO) {
        Verification verification = verificationService.addVerification(verificationRequestDTO);
        VerificationResponseDTO verificationResponseDTO = VerificationResponseDTO.fromVerification(verification);
        return ResponseEntity.ok(verificationResponseDTO);
    }

    // 리포트 조회
    @PostMapping("/report")
    @Operation(summary = "리포트 조회", description = "주어진 날짜 사이에 포함된 검증 기록을 모아 제공합니다. (리포트 범위)")
    public ResponseEntity<List<VerificationResponseDTO>> getReport(@Valid @RequestBody ReportRequestDTO reportRequestDTO) {
        return null;

        // 리포트 테이블 추가 고려 //
        // 쿼리 조회가 너무 복잡해짐!!
        // 나중에..
    }
}

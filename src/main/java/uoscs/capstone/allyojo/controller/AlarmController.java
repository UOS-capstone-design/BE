package uoscs.capstone.allyojo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uoscs.capstone.allyojo.dto.alarm.request.AlarmRequestDTO;
import uoscs.capstone.allyojo.dto.alarm.response.AlarmResponseDTO;
import uoscs.capstone.allyojo.entity.Alarm;
import uoscs.capstone.allyojo.service.AlarmService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/alarm")
@Tag(name = "알람")
public class AlarmController {
    private final AlarmService alarmService;

    // 알람 등록
    @PostMapping("/add")
    @Operation(summary = "알람 추가", description = "새로운 알람을 추가합니다.")
    public ResponseEntity<AlarmResponseDTO> addAlarm(@Valid @RequestBody AlarmRequestDTO alarmRequestDTO) {
        Alarm alarm = alarmService.addAlarm(alarmRequestDTO);
        AlarmResponseDTO alarmResponseDTO = AlarmResponseDTO.fromAlarm(alarm); // 빌더 포함 구현 필요
        return ResponseEntity.ok(alarmResponseDTO);
    }

    // 알람 조회
    @GetMapping("/{userId}")
    @Operation(summary = "알람 조회", description = "해당 유저 아이디의 알람 목록을 모두 가져옵니다.")
    public ResponseEntity<List<AlarmResponseDTO>> getAlarmsByUserId(@PathVariable("userId") Long userId) {
        List<AlarmResponseDTO> alarmResponseDTOs = alarmService
                .findAllByUserId(userId)
                .stream()
                .map(alarm -> AlarmResponseDTO.fromAlarm(alarm))
                .toList();
        return ResponseEntity.ok(alarmResponseDTOs);
    }

    // 알람 수정
    @PutMapping("/update")
    @Operation(summary = "알람 수정", description = "알람을 수정합니다.")
    public ResponseEntity<AlarmResponseDTO> updateAlarm(@Valid @RequestBody AlarmRequestDTO alarmRequestDTO) {
        Alarm updatedAlarm = alarmService.updateAlarm(alarmRequestDTO);
        AlarmResponseDTO alarmResponseDTO = AlarmResponseDTO.fromAlarm(updatedAlarm);
        return ResponseEntity.ok(alarmResponseDTO);
    }

    // 알람 삭제
    @DeleteMapping("/delete/{alarmId}")
    @Operation(summary = "알람 삭제", description = "알람을 삭제합니다.")
    public ResponseEntity<String> deleteAlarm(@PathVariable Long alarmId) {
        alarmService.deleteAlarm(alarmId);
        return ResponseEntity.ok("알람이 삭제되었습니다.");
    }
}

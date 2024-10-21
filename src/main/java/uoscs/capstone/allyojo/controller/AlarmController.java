package uoscs.capstone.allyojo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uoscs.capstone.allyojo.dto.alarm.request.AddAlarmRequestDTO;
import uoscs.capstone.allyojo.dto.alarm.response.AddAlarmResponseDTO;
import uoscs.capstone.allyojo.dto.alarm.response.AlarmListResponseDTO;
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
    public ResponseEntity<AddAlarmResponseDTO> addAlarm(@Valid @RequestBody AddAlarmRequestDTO addAlarmRequestDTO) {
        Alarm alarm = alarmService.addAlarm(addAlarmRequestDTO);
        AddAlarmResponseDTO addAlarmResponseDTO = AddAlarmResponseDTO.fromAlarm(alarm); // 빌더 포함 구현 필요
        return ResponseEntity.ok(addAlarmResponseDTO);
    }

    // 알람 조회
    @GetMapping("/{userId}")
    @Operation(summary = "알람 조회", description = "해당 유저 아이디의 알람 목록을 모두 가져옵니다.")
    public ResponseEntity<List<AlarmListResponseDTO>> getAlarmsByUserId(@PathVariable("userId") String userId) {
        return null;
    }

    // 알람 수정

    // 알람 삭제
}

package uoscs.capstone.allyojo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uoscs.capstone.allyojo.dto.alarm.request.AlarmRequestDTO;
import uoscs.capstone.allyojo.dto.alarm.response.AlarmResponseDTO;
import uoscs.capstone.allyojo.dto.guardian.request.AddUserToGuardianRequestDTO;
import uoscs.capstone.allyojo.dto.guardian.request.GuardianJoinRequestDTO;
import uoscs.capstone.allyojo.dto.guardian.request.UpdateUsersAlarmRequestDTO;
import uoscs.capstone.allyojo.dto.guardian.response.AlarmContainUserinfoResponseDTO;
import uoscs.capstone.allyojo.dto.guardian.response.GuardianResponseDTO;
import uoscs.capstone.allyojo.dto.user.response.UserResponseDTO;
import uoscs.capstone.allyojo.entity.Alarm;
import uoscs.capstone.allyojo.entity.Guardian;
import uoscs.capstone.allyojo.entity.User;
import uoscs.capstone.allyojo.service.GuardianService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/guardian")
@Tag(name = "보호자")
public class GuardianController {
    private final GuardianService guardianService;

    @PostMapping("/join")
    @Operation(summary = "보호자 회원가입", description = "보호자 회원가입합니다.")
    public ResponseEntity<GuardianResponseDTO> join(@RequestBody GuardianJoinRequestDTO guardianJoinRequestDTO) {
        Guardian guardian = guardianService.joinGuardian(guardianJoinRequestDTO);
        GuardianResponseDTO guardianResponseDTO = GuardianResponseDTO.fromGuardian(guardian);
        return ResponseEntity.ok(guardianResponseDTO);
    }

    @PutMapping("addUser")
    @Operation(summary = "유저 추가", description = "보호자가 관리하는 유저를 추가합니다.")
    public ResponseEntity<String> addUserToGuardian(@RequestBody AddUserToGuardianRequestDTO addUserToGuardianRequestDTO) {
        User updatedUser = guardianService.addUserToGuardian(addUserToGuardianRequestDTO);
        return ResponseEntity.ok("유저를 추가하였습니다. ");
    }

    @GetMapping("/{guardianName}/users")
    @Operation(summary = "보호자가 관리하는 유저 조회", description = "보호자가 관리하는 유저 리스트를 가져옵니다.")
    public ResponseEntity<List<UserResponseDTO>> getManagedUsers(@PathVariable String guardianName) {
        List<UserResponseDTO> userResponseDTOs = guardianService
                .getManagedUsers(guardianName)
                .stream()
                .map(UserResponseDTO::fromUser)
                .toList();

        return ResponseEntity.ok(userResponseDTOs);
    }

    @PostMapping("/{guardianName}/user/{username}/add")
    @Operation(summary = "보호자가 관리하는 유저 알람 추가", description = "보호자가 관리하는 유저의 알람을 추가합니다.")
    public ResponseEntity<AlarmResponseDTO> addAlarmForUser(
            @PathVariable String guardianName,
            @PathVariable String username,
            @RequestBody AlarmRequestDTO alarmRequestDTO) {
        Alarm alarm = guardianService.addAlarmForUser(guardianName, username, alarmRequestDTO);
        AlarmResponseDTO alarmResponseDTO = AlarmResponseDTO.fromAlarm(alarm);
        return ResponseEntity.ok(alarmResponseDTO);
    }

    @GetMapping("{guardianName}/alarms")
    @Operation(summary = "보호자가 관리하는 모든 유저의 모든 알람 조회", description = "보호자가 관리하는 모든 유저의 모든 알람을 가져옵니다. 단, 보호자가 생성한 것만 가져옵니다.")
    public ResponseEntity<List<AlarmContainUserinfoResponseDTO>> getAllAlarmsContainUserInfo(@PathVariable String guardianName) {
        List<AlarmContainUserinfoResponseDTO> alarms = guardianService
                .getAlarmsByGuardian(guardianName)
                .stream()
                .map(AlarmContainUserinfoResponseDTO::fromAlarm)
                .toList();

        return ResponseEntity.ok(alarms);
    }

    @GetMapping("/{guardianName}/user/{username}/alarms")
    @Operation(summary = "보호자가 관리하는 유저 한 명 알람 조회", description = "보호자가 관리하는 유저 한 명의 모든 알람을 가져옵니다.")
    public ResponseEntity<List<AlarmResponseDTO>> getAlarms(
            @PathVariable String guardianName,
            @PathVariable String username) {
        List<AlarmResponseDTO> alarms = guardianService
                .getAlarmsForUser(guardianName, username)
                .stream()
                .map(AlarmResponseDTO::fromAlarm)
                .toList();

        return ResponseEntity.ok(alarms);
    }

    @PutMapping("/{guardianName}/user/{username}/alarms/update")
    @Operation(summary = "보호자가 관리하는 유저 알람 수정", description = "보호자가 관리하는 유저의 알람을 수정합니다.")
    public ResponseEntity<AlarmResponseDTO> updatedAlarmForUser(
            @PathVariable String guardianName,
            @PathVariable String username,
            @RequestBody UpdateUsersAlarmRequestDTO dto) {

        Alarm updatedAlarm = guardianService.updateAlarmForUser(guardianName, username, dto);
        return ResponseEntity.ok(AlarmResponseDTO.fromAlarm(updatedAlarm));
    }

    @DeleteMapping("/{guardianName}/user/{username}/alarms/delete/{alarmId}")
    @Operation(summary = "보호자가 관리하는 유저 알람 삭제", description = "보호자가 관리하는 유저의 알람을 삭제합니다.")
    public ResponseEntity<String> deleteAlarmForUser(
            @PathVariable String guardianName,
            @PathVariable String username,
            @PathVariable Long alarmId) {

        guardianService.deleteAlarmForUser(guardianName, username, alarmId);
        return ResponseEntity.ok("알람이 삭제되었습니다.");
    }
}

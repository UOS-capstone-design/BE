package uoscs.capstone.allyojo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uoscs.capstone.allyojo.dto.guardian.request.GuardianJoinRequestDTO;
import uoscs.capstone.allyojo.dto.guardian.response.GuardianResponseDTO;
import uoscs.capstone.allyojo.dto.user.response.UserResponseDTO;
import uoscs.capstone.allyojo.entity.Guardian;
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
}

package uoscs.capstone.allyojo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uoscs.capstone.allyojo.dto.user.request.UserJoinRequestDTO;
import uoscs.capstone.allyojo.dto.user.response.UserResponseDTO;
import uoscs.capstone.allyojo.entity.User;
import uoscs.capstone.allyojo.repository.UserRepository;
import uoscs.capstone.allyojo.service.UserService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Tag(name = "유저")
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    @PostMapping("/join")
    @Operation(summary = "회원가입", description = "회원가입합니다.")
    @ApiResponse(responseCode = "200", description = "성공!")
    @ApiResponse(responseCode = "400", description = "파라미터 오류!")
    public ResponseEntity<UserResponseDTO> join(@Valid @RequestBody UserJoinRequestDTO userJoinRequestDTO) {
        User user = userService.joinUser(userJoinRequestDTO);
        UserResponseDTO userJoinResponseDTO = UserResponseDTO.fromUser(user);
        return ResponseEntity.ok(userJoinResponseDTO);
    }

    @GetMapping("/{username}")
    @Operation(summary = "유저 조회", description = "username을 받아와 해당 유저의 정보를 조회합니다.")
    public ResponseEntity<UserResponseDTO> getUserByUsername(@PathVariable("username") String username) {
        User user = userService.findByUsername(username);
        UserResponseDTO userResponseDTO = UserResponseDTO.fromUser(user);

        return ResponseEntity.ok(userResponseDTO);

    }

    // 유저네임 중복 확인
    @GetMapping("/{username}/check")
    @Operation(summary = "유저네임 중복 확인", description = "DB에 해당 유저네임이 있는지 조회하여 회원가입이 가능한지 리턴합니다.")
    public ResponseEntity<Map<String, Boolean>> checkUsernameDuplicate(@PathVariable String username) {
        boolean isDuplicate = userRepository.existsByUsername(username);

        Map<String, Boolean> response = new HashMap<>();
        response.put("isDuplicate", isDuplicate);

        return ResponseEntity.ok(response);
    }

    // 테스트용
    @GetMapping("/jwtTest")
    public String test() {
        System.out.println("userService = " + userService);
        return "JWT 인증 성공.";
    }

    // 테스트용
    @GetMapping("/testGuardian")
    public String testGuardian() {
        System.out.println("userService = " + userService);
        return "보호자 권한 인증 성공.";
    }

    // 테스트용
    @GetMapping("/join")
    public String showUserJoinForm(Model model) {
        return "userJoin";
    }
}

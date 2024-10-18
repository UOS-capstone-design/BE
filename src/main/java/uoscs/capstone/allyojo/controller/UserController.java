package uoscs.capstone.allyojo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uoscs.capstone.allyojo.dto.request.UserJoinRequestDTO;
import uoscs.capstone.allyojo.dto.response.UserJoinResponseDTO;
import uoscs.capstone.allyojo.entity.User;
import uoscs.capstone.allyojo.repository.UserRepository;
import uoscs.capstone.allyojo.service.UserService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/join")
    public ResponseEntity<UserJoinResponseDTO> join(@RequestBody UserJoinRequestDTO userJoinRequestDTO) {
        User user = userService.joinUser(userJoinRequestDTO);
        UserJoinResponseDTO userJoinResponseDTO = UserJoinResponseDTO.fromUser(user);
        log.info("join실행");
        return ResponseEntity.ok(userJoinResponseDTO);

    }

    @GetMapping("/jwtTest")
    public String test() {
        System.out.println("userService = " + userService);
        return "JWT 인증 성공.";
    }

    @GetMapping("/join")
    public String showUserJoinForm(Model model) {
        return "userJoin";
    }
}

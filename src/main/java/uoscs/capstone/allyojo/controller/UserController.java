package uoscs.capstone.allyojo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import uoscs.capstone.allyojo.dto.request.UserJoinRequestDTO;
import uoscs.capstone.allyojo.entity.User;
import uoscs.capstone.allyojo.repository.UserRepository;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;
   // private final UserService userService;
//
//    @PostMapping("join")
//    public ResponseEntity<UserJoinRequestDTO> join(@RequestBody UserJoinRequestDTO userJoinRequestDTO) {
//
//
//    }

}

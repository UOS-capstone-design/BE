package uoscs.capstone.allyojo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import uoscs.capstone.allyojo.dto.request.UserJoinRequestDTO;
import uoscs.capstone.allyojo.entity.User;
import uoscs.capstone.allyojo.entity.UserGrade;
import uoscs.capstone.allyojo.exception.user.UserNotFoundException;
import uoscs.capstone.allyojo.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // 회원가입
    public User joinUser(UserJoinRequestDTO userJoinRequestDTO) {
        // 비밀번호 인코딩
        String encodedPassword = bCryptPasswordEncoder.encode(userJoinRequestDTO.getPassword());

        User user =
                User.builder()
                        .username(userJoinRequestDTO.getUsername())
                        .password(encodedPassword)
                        .name(userJoinRequestDTO.getName())
                        .userGrade(UserGrade.BASIC)
                        .phoneNumber(userJoinRequestDTO.getPhoneNumber())
                        .guardianPhoneNumber(userJoinRequestDTO.getGuardianPhoneNumber())
                        .build();

        return userRepository.save(user);
    }
    // 로그인은 /login으로 요청 보내주세요. JwtAuthenticationFilter가 처리

    // 유저 조회
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException());
    }


}

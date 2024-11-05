package uoscs.capstone.allyojo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import uoscs.capstone.allyojo.dto.guardian.request.GuardianJoinRequestDTO;
import uoscs.capstone.allyojo.entity.Guardian;
import uoscs.capstone.allyojo.entity.User;
import uoscs.capstone.allyojo.exception.guardian.GuardianNotFoundException;
import uoscs.capstone.allyojo.exception.user.UserNotFoundException;
import uoscs.capstone.allyojo.repository.GuardianRepository;
import uoscs.capstone.allyojo.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class GuardianService {
    private final GuardianRepository guardianRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;

    // 보호자 회원가입
    public Guardian joinGuardian(GuardianJoinRequestDTO guardianJoinRequestDTO) {
        String encodedPassword = bCryptPasswordEncoder.encode(guardianJoinRequestDTO.getPassword());

        Guardian guardian =
                Guardian.builder()
                    .guardianName(guardianJoinRequestDTO.getGuardianName())
                    .password(encodedPassword)
                    .name(guardianJoinRequestDTO.getName())
                    .phoneNumber(guardianJoinRequestDTO.getPhoneNumber())
                    .build();

        // 노인 아이디를 입력했다면
        if (guardianJoinRequestDTO.getSeniorName() != null) {
            // 노인 사용자 찾기
            User user = userRepository.findByUsername(guardianJoinRequestDTO.getSeniorName())
                    .orElseThrow(UserNotFoundException::new);

            // 기존 User 엔티티 복사 및 Guardian 관계 설정
            User updatedUser = User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .name(user.getName())
                    .userGrade(user.getUserGrade())
                    .phoneNumber(user.getPhoneNumber())
                    .guardian(guardian)  // 관계 설정
                    .build();

            userRepository.save(updatedUser);
            guardian.addManagedUser(updatedUser);
        }

        return guardianRepository.save(guardian);

    }

    // 보호자가 관리하는 노인 리스트 조회
    public List<User> getManagedUsers(String guardianName) { // guardianName은 로그인할 때 쓰는 아이디
        Guardian guardian = guardianRepository.findByGuardianName(guardianName)
                .orElseThrow(GuardianNotFoundException::new);

        return guardian.getUsers();
    }
}

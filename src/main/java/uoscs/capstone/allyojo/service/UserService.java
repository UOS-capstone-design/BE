package uoscs.capstone.allyojo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uoscs.capstone.allyojo.dto.request.UserJoinRequestDTO;
import uoscs.capstone.allyojo.entity.User;
import uoscs.capstone.allyojo.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
//
//    public User joinUser(UserJoinRequestDTO userJoinRequestDTO) {
//        User user =
//                User.builder()
//                        .username()
//    }
}

package uoscs.capstone.allyojo.config.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import uoscs.capstone.allyojo.entity.User;
import uoscs.capstone.allyojo.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userEntity = userRepository.findByUsername(username);
        // 유저 없는 경우 예외
        if (userEntity == null) {
            throw new UsernameNotFoundException(username);
        }
        return new PrincipalDetails(userEntity);
    }
}

package uoscs.capstone.allyojo.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import uoscs.capstone.allyojo.entity.Guardian;
import uoscs.capstone.allyojo.exception.guardian.GuardianNotFoundException;
import uoscs.capstone.allyojo.repository.GuardianRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class GuardianDetailsService implements UserDetailsService {

    private final GuardianRepository guardianRepository;

    @Override
    public UserDetails loadUserByUsername(String guardianName) throws UsernameNotFoundException {
        Guardian guardian = guardianRepository.findByGuardianName(guardianName)
                .orElseThrow(() -> new GuardianNotFoundException());

        return new GuardianDetails(guardian);
    }
}

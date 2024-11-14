package uoscs.capstone.allyojo.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class GuardianAuthenticationProvider extends DaoAuthenticationProvider {

    @Autowired
    public GuardianAuthenticationProvider(GuardianDetailsService guardianDetailsService,
                                          PasswordEncoder passwordEncoder) {
        this.setUserDetailsService(guardianDetailsService);
        this.setPasswordEncoder(passwordEncoder);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String details = authentication.getDetails() != null ? authentication.getDetails().toString() : "";
        if (!"GUARDIAN_LOGIN".equals(details)) {
            return null;  // 다른 provider가 처리하도록 null 반환
        }
        log.info("guardianAuthenticationProvider:: 보호자 login");
        return super.authenticate(authentication);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return super.supports(authentication);
    }
}
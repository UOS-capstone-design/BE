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
public class UserAuthenticationProvider extends DaoAuthenticationProvider {

    @Autowired
    public UserAuthenticationProvider(PrincipalDetailsService principalDetailsService,
                                      PasswordEncoder passwordEncoder) {
        this.setUserDetailsService(principalDetailsService);
        this.setPasswordEncoder(passwordEncoder);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String details = authentication.getDetails() != null ? authentication.getDetails().toString() : "";
        if (!"USER_LOGIN".equals(details)) {
            return null;  // 다른 provider가 처리하도록 null 반환
        }
        return super.authenticate(authentication);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return super.supports(authentication);
    }
}
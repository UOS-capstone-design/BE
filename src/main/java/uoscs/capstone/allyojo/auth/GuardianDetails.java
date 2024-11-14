package uoscs.capstone.allyojo.auth;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import uoscs.capstone.allyojo.entity.Guardian;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Data
@RequiredArgsConstructor
public class GuardianDetails implements UserDetails {
    private final Guardian guardian;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_GUARDIAN"));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getPassword() {
        return guardian.getPassword();
    }

    @Override
    public String getUsername() {
        return guardian.getGuardianName();
    }
}

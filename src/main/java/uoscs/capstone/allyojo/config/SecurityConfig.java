package uoscs.capstone.allyojo.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import uoscs.capstone.allyojo.auth.GuardianAuthenticationProvider;
import uoscs.capstone.allyojo.auth.GuardianDetailsService;
import uoscs.capstone.allyojo.auth.PrincipalDetailsService;
import uoscs.capstone.allyojo.auth.UserAuthenticationProvider;
import uoscs.capstone.allyojo.jwt.JwtAuthenticationFilter;
import uoscs.capstone.allyojo.jwt.JwtAuthorizationFilter;
import uoscs.capstone.allyojo.repository.GuardianRepository;
import uoscs.capstone.allyojo.repository.UserRepository;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsConfig corsConfig;
    private final UserRepository userRepository;
    private final GuardianRepository guardianRepository;
    private final UserAuthenticationProvider userAuthenticationProvider;
    private final GuardianAuthenticationProvider guardianAuthenticationProvider;
    private final PrincipalDetailsService principalDetailsService;
    private final GuardianDetailsService guardianDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);

        // Provider 등록
        builder.authenticationProvider(userAuthenticationProvider)
                .authenticationProvider(guardianAuthenticationProvider);

        AuthenticationManager authenticationManager = builder.build();
        http.authenticationManager(authenticationManager);

        // JWT 필터 설정
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager);
        jwtAuthenticationFilter.setFilterProcessesUrl("/login/**"); // 로그인 URL 패턴 설정

        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> // stateless session (JWT)
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilter(corsConfig.corsFilter())
                .addFilter(jwtAuthenticationFilter)
                .addFilterBefore(new JwtAuthorizationFilter(authenticationManager, userRepository, guardianRepository), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/user/join").permitAll()
                        .requestMatchers("/guardian/join").permitAll()
                        .requestMatchers("/login/user", "/login/guardian").permitAll()
                        .requestMatchers("/test").hasRole("PREMIUM")
                        .requestMatchers("/user/testGuardian").hasRole("GUARDIAN")
                        .requestMatchers("/user/jwtTest").authenticated()
                        .anyRequest().permitAll()) // 나중에 Authenticated로
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
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
import uoscs.capstone.allyojo.config.jwt.JwtAuthenticationFilter;
import uoscs.capstone.allyojo.config.jwt.JwtAuthorizationFilter;
import uoscs.capstone.allyojo.repository.UserRepository;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsConfig corsConfig;
    private final UserRepository userRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // authenticationManager 빌드
        AuthenticationManagerBuilder sharedObject = http.getSharedObject(AuthenticationManagerBuilder.class);
        AuthenticationManager authenticationManager = sharedObject.build();
        http.authenticationManager(authenticationManager);

        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> // stateless session (JWT)
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilter(corsConfig.corsFilter())
                //.addFilter(new JwtAuthenticationFilter(authenticationManager))
                //UsernamePasswordAuthenticationFilter 위치에 JwtAuthenticationFilter 추가
                // 토큰이 없으면 로그인 후 토큰 발급
                .addFilter(new JwtAuthenticationFilter(authenticationManager))
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/user/join").permitAll()
                        .requestMatchers("test").hasRole("PREMIUM")
                        .anyRequest().authenticated())

                 // 얘가 문제임
                //.addFilter(new JwtAuthorizationFilter(authenticationManager, userRepository))
                //BasicAuthenticationFilter 대체, JwtAuthenticationFilter보다 먼저 실행
                // 토큰이 있는지 먼저 검증
                .addFilterBefore(new JwtAuthorizationFilter(authenticationManager, userRepository), UsernamePasswordAuthenticationFilter.class)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }
}

package uoscs.capstone.allyojo.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import uoscs.capstone.allyojo.auth.GuardianDetails;
import uoscs.capstone.allyojo.auth.PrincipalDetails;
import uoscs.capstone.allyojo.dto.user.request.LoginRequestDTO;
import uoscs.capstone.allyojo.entity.User;

import java.io.IOException;
import java.util.Date;

// /login 요청
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager; // authenticationManager 주입

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        log.info("로그인 진행 중 - JwtAuthenticationFilter - attemptAuthentication");
        log.info("authenticationManager = {}", authenticationManager);
        try {
            String loginUrl = request.getRequestURI();
            LoginRequestDTO loginRequestDTO = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDTO.class);
            UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDTO.getUsername(),
                            loginRequestDTO.getPassword()
                    ); // 토큰 제작

            // URL에 따라 적절한 details 설정
            if (request.getRequestURI().contains("/login/guardian")) {
                token.setDetails(new WebAuthenticationDetails(request) {
                    @Override
                    public String toString() {
                        return "GUARDIAN_LOGIN";
                    }
                });
            } else {
                token.setDetails(new WebAuthenticationDetails(request) {
                    @Override
                    public String toString() {
                        return "USER_LOGIN";
                    }
                });
            }

            return authenticationManager.authenticate(token);

        } catch (IOException e) {
            throw new RuntimeException(e); // 추후 exceptionHandler 구현
        } catch (UsernameNotFoundException e) {
            log.error("usernameNotFoundException", e);
            throw new RuntimeException(e);
        }
    }

    // 로그인 성공 시 실행
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult)
            throws IOException, ServletException {
        Object principal = authResult.getPrincipal();
        String username = null;
        String role = null;

        if (principal instanceof PrincipalDetails) {
            username = ((PrincipalDetails) principal).getUser().getUsername();
            role = "ROLE_USER";
        } else if (principal instanceof GuardianDetails) {
            username = ((GuardianDetails) principal).getGuardian().getGuardianName();
            role = "ROLE_GUARDIAN";
        }

        // HMAC512 방식. 추후 RSA 변경 가능성
        String jwtToken = JWT.create()
                .withSubject("JWT token")
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME))
                .withClaim("username", username)
                //.withClaim("userGrade", principalDetails.getUser().getUserGrade().name())
                .withClaim("role", role)
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));
        log.info("로그인 성공 - JwtAuthenticationFilter - successfulAuthentication");
        log.info("jwtToken: {}", jwtToken);
        log.info("테스트용 토큰 복붙: {}", "Bearer " + jwtToken);
        response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + jwtToken); // 헤더에 토큰 추가
    }

    // 로그인 실패 시 실행
    // 추후 구현
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed)
            throws IOException, ServletException {
        log.error("로그인 실패", failed);

//        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
//        response.setContentType("application/json; charset=utf-8");
        // 401 에러 담겨서 갑니다. 처리 부탁드려요
        super.unsuccessfulAuthentication(request, response, failed);
    }
}

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
import uoscs.capstone.allyojo.auth.PrincipalDetails;
import uoscs.capstone.allyojo.entity.User;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
        try {
            ObjectMapper mapper = new ObjectMapper();
            User user = mapper.readValue(request.getInputStream(), User.class);
            UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()); // 토큰 제작

            // 인증 성공 시 authentication 리턴. 이 부분 예외 처리해야 하나?
            Authentication authentication = authenticationManager.authenticate(token); // 토큰을 포함하여 인증 진행
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            log.info("principalDetails: {}", principalDetails);

            return authentication;

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
        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();

        // HMAC512 방식. 추후 RSA 변경 가능성
        String jwtToken = JWT.create()
                .withSubject("JWT token")
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME))
                .withClaim("id", principalDetails.getUser().getUserId())
                .withClaim("username", principalDetails.getUser().getUsername())
                .withClaim("userGrade", principalDetails.getUser().getUserGrade().name())
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

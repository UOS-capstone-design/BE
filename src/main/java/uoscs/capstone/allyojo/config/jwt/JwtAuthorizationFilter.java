package uoscs.capstone.allyojo.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import uoscs.capstone.allyojo.config.auth.PrincipalDetails;
import uoscs.capstone.allyojo.entity.User;
import uoscs.capstone.allyojo.repository.UserRepository;

import java.io.IOException;

// 권한 요청 시 실행
@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
    private UserRepository userRepository;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
        super(authenticationManager);
        this.userRepository = userRepository;
    }

    // 헤더 Bearer가 아닌 경우 예외처리해야 함
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String jwtHeader = request.getHeader(JwtProperties.HEADER_STRING);
        log.info("인증이 필요한 요청입니다. (BasicAuthenticationFilter -> JwtAuthorizationFilter)");
        // jwtHeader가 Bearer 방식으로 정상적으로 왔다면
        if (jwtHeader != null && jwtHeader.startsWith(JwtProperties.TOKEN_PREFIX)) {
            // prefix를 자르고 헤더 부분만 (Bearer 자름)
            String jwtToken = jwtHeader.substring(JwtProperties.TOKEN_PREFIX.length());

            // 서명 후 유저네임을 claim으로부터 가져옴
            String username = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET))
                    .build().verify(jwtToken).getClaim("username").asString();
            log.info("JwtAuthorizationFilter" + "USERNAME: {}", username);

            // 유저네임이 널인 경우 예외처리해야 함.
            if (username != null) {
                User userEntity = userRepository.findByUsername(username);
                // Authentication 객체 만들기
                PrincipalDetails principalDetails = new PrincipalDetails(userEntity);
                Authentication authentication =
                        new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());

                // 시큐리티 세션에 Authentication 객체를 넣어줌
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("인증 성공" + " Authentication: {}", authentication);
                chain.doFilter(request, response);
            }
        } else { // jwt 헤더가 없는 경우 -> 다음 필터로 이동
            chain.doFilter(request, response);
            log.info("jwt 헤더가 없습니다.");
        }
    }
}

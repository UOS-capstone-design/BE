package uoscs.capstone.allyojo.jwt;

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
import uoscs.capstone.allyojo.auth.GuardianDetails;
import uoscs.capstone.allyojo.auth.PrincipalDetails;
import uoscs.capstone.allyojo.entity.Guardian;
import uoscs.capstone.allyojo.entity.User;
import uoscs.capstone.allyojo.exception.global.ErrorCode;
import uoscs.capstone.allyojo.exception.global.JwtException;
import uoscs.capstone.allyojo.repository.GuardianRepository;
import uoscs.capstone.allyojo.repository.UserRepository;

import java.io.IOException;
import java.util.Optional;

// 권한 요청 시 실행
@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private UserRepository userRepository;
    private GuardianRepository guardianRepository;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository, GuardianRepository guardianRepository) {
        super(authenticationManager);
        this.userRepository = userRepository;
        this.guardianRepository = guardianRepository;
    }

    // 헤더 Bearer가 아닌 경우 예외처리해야 함
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String jwtHeader = request.getHeader(JwtProperties.HEADER_STRING);
        log.info("1. 권한 인증이 필요한 요청입니다. (BasicAuthenticationFilter -> JwtAuthorizationFilter)");
        log.info("jwtHeader = {}", jwtHeader);
        log.info("2. 헤더 확인");
        if (jwtHeader == null || !jwtHeader.startsWith(JwtProperties.TOKEN_PREFIX)) {
            chain.doFilter(request, response); // jwt 헤더가 없는 요청의 경우 다음 필터로 넘깁니다.
            log.info("2-11111. jwt 헤더가 없습니다.");
            return;
        }

        // jwtHeader가 Bearer 방식으로 정상적으로 왔다면
        log.info("3. JWT 토큰 검증. 정상적인 사용자인지, 권한이 맞는지 확인합니다.");
        // prefix를 자르고 헤더 부분만 (Bearer 자름)
        String jwtToken = jwtHeader.substring(JwtProperties.TOKEN_PREFIX.length());
        String username = null;
        String role = null;
        try {
             username = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET))
                    .build()
                    .verify(jwtToken)
                    .getClaim("username")
                    .asString();
            log.info("username = {}", username);

             role = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET))
                    .build()
                    .verify(jwtToken)
                    .getClaim("role")
                    .asString();
            log.info("role = {}", role);

        } catch (Exception e) {
            // jwt 서명 에러
            throw new JwtException(ErrorCode.JWT_SIGNATURE_ERROR);
            //throw new exception(exmessage.jwt.errorformat);
        }

        // else 처리해야 함.
        if (username != null && role != null) {
            if ("ROLE_USER".equals(role)) {
                Optional<User> user = userRepository.findByUsername(username);
                user.ifPresent(u -> {
                    PrincipalDetails principalDetails = new PrincipalDetails(u);
                    Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                });
            } else if ("ROLE_GUARDIAN".equals(role)) {
                Optional<Guardian> guardian = guardianRepository.findByGuardianName(username);
                guardian.ifPresent(g -> {
                    GuardianDetails guardianDetails = new GuardianDetails(g);
                    Authentication authentication = new UsernamePasswordAuthenticationToken(guardianDetails, null, guardianDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                });
            }
        }
        chain.doFilter(request, response);
    }
}

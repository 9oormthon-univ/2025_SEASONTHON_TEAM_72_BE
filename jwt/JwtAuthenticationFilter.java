package goorm.hackathon.pizza.jwt;

import goorm.hackathon.pizza.entity.UserEntity;
import goorm.hackathon.pizza.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider, UserRepository userRepository) {
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        // Authorization 헤더 확인
        String header = req.getHeader(HttpHeaders.AUTHORIZATION);

        // Bearer <토큰> 형식일 때만 처리
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7); // "Bearer " 이후 부분만 추출
            try {
                // 토큰에서 userId 추출
                Long userId = tokenProvider.getUserId(token);

                // DB에서 사용자 조회
                UserEntity user = userRepository.findById(userId).orElse(null);
                if (user != null) {
                    // DB role 값이 "USER"면 "ROLE_USER"로 변환
                    String role = user.getRole();
                    String springRole = (role != null && role.startsWith("ROLE_")) ? role : "ROLE_" + role;

                    // 권한 리스트 생성
                    List<SimpleGrantedAuthority> authorities =
                            (role != null)
                                    ? List.of(new SimpleGrantedAuthority(springRole))
                                    : Collections.emptyList();

                    // principal 에 UserEntity 그대로 넣기 > @AuthenticationPrincipal 로 접근 가능
                    var auth = new UsernamePasswordAuthenticationToken(user, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (JwtException ignored) {
                // 토큰이 잘못된 경우 무시 → 이후 Security에서 401 처리
            }
        }

        // 다음 필터로 계속 진행
        chain.doFilter(req, res);
    }
}

package goorm.hackathon.pizza.config;

import goorm.hackathon.pizza.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF/CORS/세션
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(req -> {
                    CorsConfiguration c = new CorsConfiguration();
                    c.setAllowedOrigins(List.of("*"));
                    c.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
                    c.setAllowedHeaders(List.of("*"));
                    c.setAllowCredentials(false);
                    return c;
                }))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 401 시 브라우저 Basic 팝업 방지
                .exceptionHandling(e -> e.authenticationEntryPoint((req, res, ex) -> res.sendError(401)))

                // 인가 규칙
                .authorizeHttpRequests(auth -> auth
                        // Swagger / 정적
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()

                        // 헬스체크/에러
                        .requestMatchers("/actuator/**", "/error").permitAll()

                        // Auth API
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/signup", "/api/v1/auth/login").permitAll()
                        .requestMatchers("/api/v1/auth/**").permitAll()

                        // 초대코드 조회는 비로그인 허용 (대소문자 입력 허용은 컨트롤러/서비스에서 처리)
                        .requestMatchers(HttpMethod.GET, "/api/v1/invites/**").permitAll()

                        // 초대코드 생성/재발급은 로그인 필요
                        .requestMatchers(HttpMethod.POST, "/api/v1/settlements/*/invites").authenticated()

                        // 보호 API
                        .requestMatchers("/api/v1/users/me").authenticated()

                        // 그 외 전부 보호
                        .anyRequest().authenticated()
                );

        // JWT 필터
        http.addFilterBefore(jwtFilter, BasicAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }
}

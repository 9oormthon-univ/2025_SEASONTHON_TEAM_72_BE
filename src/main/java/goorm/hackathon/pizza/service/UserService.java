// src/main/java/goorm/hackathon/pizza/service/UserService.java
package goorm.hackathon.pizza.service;

import goorm.hackathon.pizza.dto.user.UserMeResponse;
import goorm.hackathon.pizza.entity.User;
import goorm.hackathon.pizza.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // 현재 로그인한 유저 정보 가져오기
    public UserMeResponse getMe() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // principal이 userId(Long)을 제공하는 타입
        if (principal instanceof UserIdSupplier p) {
            Long userId = p.userId();
            User u = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalStateException("User not found: " + userId));
            return toDto(u);
        }

        // principal이 UserDetails → email 기반 조회
        if (principal instanceof UserDetails ud) {
            String email = ud.getUsername();
            User u = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalStateException("User not found: " + email));
            return toDto(u);
        }

        // principal이 단순 String(email)일 경우
        if (principal instanceof String email) {
            User u = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalStateException("User not found: " + email));
            return toDto(u);
        }

        // 위 세 가지 케이스 외에는 지원 안함
        throw new IllegalStateException("Unsupported principal type: " + principal.getClass());
    }

    // UserEntity → UserMeResponse 변환
    public UserMeResponse toDto(User u) {
        return UserMeResponse.builder()
                .userId(u.getUserId())
                .email(u.getEmail())
                .nickname(u.getNickname())
                .provider(u.getProvider())
                .role(u.getRole())
                .createdAt(u.getCreatedAt())
                .updatedAt(u.getUpdatedAt())
                .build();
    }

    // principal이 userId를 직접 제공할 수 있도록 하는 인터페이스
    public interface UserIdSupplier {
        Long userId();
    }
}

package goorm.hackathon.pizza.dto.user;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record UserMeResponse(
        Long userId,        // PK
        String email,       // 이메일
        String nickname,    // 닉네임
        String provider,    // 가입 제공자
        String role,        // 권한 (Admin, User)
        LocalDateTime createdAt, // 생성 시각
        LocalDateTime updatedAt  // 수정 시각
) {}

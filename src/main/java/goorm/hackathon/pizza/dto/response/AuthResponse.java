package goorm.hackathon.pizza.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private Long userId;
    private String email;
    private String nickname;
    private String token; // JWT 토큰
}

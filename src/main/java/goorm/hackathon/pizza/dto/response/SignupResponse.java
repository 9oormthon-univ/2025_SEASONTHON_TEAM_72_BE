package goorm.hackathon.pizza.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignupResponse {
    private Long userId;
    private String email;
    private String nickname;
    private String firebaseUid;
    private String accessToken;      // JWT
}

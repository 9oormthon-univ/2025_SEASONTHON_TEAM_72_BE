package goorm.hackathon.pizza.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class LoginRequest {

    // 로그인용 이메일 (빈 값 금지 + 이메일 형식 검증)
    @Email
    @NotBlank
    private String email;

    // 로그인용 비밀번호 (빈 값 금지)
    @NotBlank
    private String password;
}

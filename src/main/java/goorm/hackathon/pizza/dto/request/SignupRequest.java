package goorm.hackathon.pizza.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SignupRequest {

    // 회원가입 이메일 (빈 값 금지 + 이메일 형식 체크)
    @Email
    @NotBlank
    private String email;

    // 비밀번호 (빈 값 금지, 최소 6자 ~ 최대 50자)
    @NotBlank
    @Size(min = 6, max = 50)
    private String password;

    // 닉네임 (빈 값 금지, 길이 1~50자, 중복 가능)
    @NotBlank
    @Size(min = 1, max = 50)
    private String nickname;
}

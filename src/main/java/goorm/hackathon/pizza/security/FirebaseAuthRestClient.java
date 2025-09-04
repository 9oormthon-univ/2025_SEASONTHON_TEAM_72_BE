package goorm.hackathon.pizza.security;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class FirebaseAuthRestClient {

    private final RestTemplate restTemplate;
    private final String apiKey;

    // Firebase API 키는 application.properties 에서 주입
    public FirebaseAuthRestClient(RestTemplate restTemplate,
                                  @Value("${firebase.api-key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
    }

    // Firebase 로그인 요청 DTO
    @Data
    private static class SignInRequest {
        private final String email;
        private final String password;
        private final boolean returnSecureToken = true;
    }

    // Firebase 로그인 응답 DTO
    @Data
    public static class SignInResponse {
        private String idToken;     // Firebase ID 토큰
        private String refreshToken;
        private String localId;     // Firebase UID
        private String email;
        private String expiresIn;
    }

    // 이메일/비번으로 Firebase api 호출
    public SignInResponse signInWithPassword(String email, String password) {
        String url = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + apiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        var entity = new HttpEntity<>(new SignInRequest(email, password), headers);
        ResponseEntity<SignInResponse> res = restTemplate.exchange(url, HttpMethod.POST, entity, SignInResponse.class);
        return res.getBody();
    }
}

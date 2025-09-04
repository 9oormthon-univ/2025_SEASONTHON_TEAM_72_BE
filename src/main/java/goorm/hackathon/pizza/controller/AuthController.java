package goorm.hackathon.pizza.controller;

import goorm.hackathon.pizza.dto.request.LoginRequest;
import goorm.hackathon.pizza.dto.request.SignupRequest;
import goorm.hackathon.pizza.dto.response.AuthResponse;
import goorm.hackathon.pizza.dto.response.SignupResponse;
import goorm.hackathon.pizza.service.AuthService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@SecurityRequirement(name = "BearerAuth") // Swagger
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // 회원가입 API: POST /api/v1/auth/signup
    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest req) {
        var res = authService.signup(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(res); // 201 Created 반환
    }

    // 로그인 API: POST /api/v1/auth/login
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req)); // 200 OK + JWT 토큰 반환
    }
}

package goorm.hackathon.pizza.controller;

import goorm.hackathon.pizza.dto.user.UserMeResponse;
import goorm.hackathon.pizza.entity.UserEntity;
import goorm.hackathon.pizza.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth") // Swagger
public class UserController {

    private final UserService userService;

    // 현재 로그인한 사용자 정보 조회 API
    // GET /api/v1/users/me
    // JWT 인증 필터를 거쳐 SecurityContext에 저장된 UserEntity가 들어옴
    @GetMapping("/me")
    public UserMeResponse me(@AuthenticationPrincipal UserEntity user) {
        // UserEntity → UserMeResponse DTO 변환
        return userService.toDto(user);
    }
}

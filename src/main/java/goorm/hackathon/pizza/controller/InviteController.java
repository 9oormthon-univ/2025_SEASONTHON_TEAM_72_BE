package goorm.hackathon.pizza.controller;

import goorm.hackathon.pizza.dto.response.InviteResponse;
import goorm.hackathon.pizza.entity.InviteEntity;
import goorm.hackathon.pizza.entity.User;
import goorm.hackathon.pizza.service.InviteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class InviteController {

    private final InviteService inviteService;

    // 공유 아이콘 클릭 시: 활성 초대가 있으면 그걸, 없으면 새로 생성해서 반환
    @PostMapping("/settlements/{settlementId}/invites")
    public ResponseEntity<InviteResponse> createOrFetch(
            @PathVariable Long settlementId,
            @AuthenticationPrincipal User user  // 필터에서 세팅한 principal 그대로 받기
    ) {
        if (user == null) { // 혹시 인증 누락 시 방어
            return ResponseEntity.status(401).build();
        }

        Long userId = user.getUserId(); //  DB user_id
        InviteEntity inv = inviteService.createOrFetch(settlementId, userId);

        String base = System.getenv().getOrDefault("FRONTEND_BASE_URL", "https://app.example.com");
        String url  = base + "/join/" + inv.getCode();
        var fmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        InviteResponse resp = new InviteResponse(
                inv.getCode(),
                url,
                inv.getExpiresAt() != null ? inv.getExpiresAt().format(fmt) : null,
                true // 기존 있으면 그대로, 없으면 새로 생성 – 프론트용 플래그
        );
        return ResponseEntity.ok(resp);
    }

    // 초대 코드 조회 (대소문자 허용)
    @GetMapping("/invites/{code}")
    public ResponseEntity<InviteResponse> getByCode(@PathVariable String code) {
        var inv = inviteService.getActiveByCode(code);
        String base = System.getenv().getOrDefault("FRONTEND_BASE_URL", "https://app.example.com");
        String url = base + "/join/" + inv.getCode();
        var fmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        return ResponseEntity.ok(
                new InviteResponse(
                        inv.getCode(),
                        url,
                        inv.getExpiresAt() != null ? inv.getExpiresAt().format(fmt) : null,
                        true
                )
        );
    }
}

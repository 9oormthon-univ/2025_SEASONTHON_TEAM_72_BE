package goorm.hackathon.pizza.controller;

import goorm.hackathon.pizza.dto.request.ReminderRequest;
import goorm.hackathon.pizza.dto.response.notification.ReminderResponse;
import goorm.hackathon.pizza.service.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/settlements")
@RequiredArgsConstructor
public class ReminderController {

    private final ReminderService reminderService;

    /**
     * 한 명에게 독촉하기
     * @param settlementId 정산 ID
     * @param request 독촉 대상 userId
     * @param requesterId 로그인한 사용자 ID (JWT → principal.userId 추출)
     */
    @PostMapping("/{sid}/remind")
    public ResponseEntity<ReminderResponse> remindOne(
            @PathVariable("sid") Long settlementId,
            @RequestBody ReminderRequest request,
            @AuthenticationPrincipal(expression = "userId") Long requesterId
    ) {
        ReminderResponse res = reminderService.remindOne(
                settlementId,
                request.getUserId(),
                requesterId
        );
        return ResponseEntity.ok(res);
    }
}

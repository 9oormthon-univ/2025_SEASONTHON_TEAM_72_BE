package goorm.hackathon.pizza.controller;

import goorm.hackathon.pizza.dto.request.PaymentRequest;
import goorm.hackathon.pizza.dto.response.notification.NotificationSimpleResponse;
import goorm.hackathon.pizza.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/settlements/{sid}/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // 공통: requesterId 안전하게 꺼내기 (expression → fallback)
    private Long currentUserId(@AuthenticationPrincipal(expression = "userId") Object principalFromExpr) {
        if (principalFromExpr instanceof Long l) return l;            // CustomUserDetails.userId 가 Long 인 경우
        if (principalFromExpr instanceof Integer i) return i.longValue();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName() != null) {
            try {
                return Long.valueOf(auth.getName());                  // JWT subject가 "1001" 처럼 숫자 문자열인 경우
            } catch (NumberFormatException ignore) {}
        }
        return null; // 최종 실패 시 null
    }

    /** 총괄자: 특정 참가자 입금 완료 처리 */
    @PostMapping("/confirm")
    public ResponseEntity<NotificationSimpleResponse> confirm(
            @PathVariable("sid") Long settlementId,
            @RequestBody PaymentRequest request,
            // principal 이 User/CustomUserDetails 라도 userId만 뽑아옴
            @AuthenticationPrincipal(expression = "userId") Long requesterId
    ) {
        var res = paymentService.confirmDeposit(settlementId, request.getUserId(), requesterId);
        return ResponseEntity.ok(res);
    }
    /** 총괄자: 특정 참가자 입금 취소 처리 */
    @PostMapping("/cancel")
    public ResponseEntity<NotificationSimpleResponse> cancel(
            @PathVariable("sid") Long settlementId,
            @RequestBody PaymentRequest request,
            @AuthenticationPrincipal(expression = "userId") Object principalFromExpr
    ) {
        Long requesterId = currentUserId(principalFromExpr);
        NotificationSimpleResponse body =
                paymentService.cancelDeposit(settlementId, request.getUserId(), requesterId);

        return ResponseEntity.ok(body);
    }
}

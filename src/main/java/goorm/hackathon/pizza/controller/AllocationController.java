package goorm.hackathon.pizza.controller;

import goorm.hackathon.pizza.dto.request.AllocateItemRequest;
import goorm.hackathon.pizza.dto.response.*;
import goorm.hackathon.pizza.entity.User;
import goorm.hackathon.pizza.service.AllocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/allocations")
public class AllocationController {

    private final AllocationService allocationService;

    // 1. 정산별 품목 목록 조회 API
    @GetMapping("/settlements/{settlementId}/items")
    public ResponseEntity<List<AllocationItemResponse>> getSettlementItems(@PathVariable Long settlementId) {
        return ResponseEntity.ok(allocationService.getItemList(settlementId));
    }

    // 2. 품목별 유저들의 정산 내역 목록 조회 API
    @GetMapping("/items/{itemId}/users")
    public ResponseEntity<List<AllocationUserDetailResponse>> getAllocationsForItem(@PathVariable Long itemId) {
        return ResponseEntity.ok(allocationService.getAllocationsForItem(itemId));
    }

    // 3. 품목별 유저 개인 정산 저장 API
    @PostMapping("/settlements/{settlementId}/items/{itemId}")
    public ResponseEntity<AllocationUserDetailResponse> allocateItem(
            @PathVariable Long settlementId,
            @PathVariable Long itemId,
            @Valid @RequestBody AllocateItemRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(allocationService.allocateItem(settlementId, itemId, request, user));
    }

    // 4. 유저별 개인 영수증 조회 API
    @GetMapping("/settlements/{settlementId}/my-receipt")
    public ResponseEntity<UserReceiptResponse> getMyReceipt(
            @PathVariable Long settlementId,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(allocationService.getUserReceipt(settlementId, user));
    }

    // 5. 완료 및 미완료 품목 개수 조회 API
    @GetMapping("/settlements/{settlementId}/status-count")
    public ResponseEntity<AllocationStatusCountResponse> getAllocationStatusCount(@PathVariable Long settlementId) {
        return ResponseEntity.ok(allocationService.getAllocationStatusCount(settlementId));
    }

    // 6. 정산 전체 요약 조회 API
    @GetMapping("/settlements/{settlementId}/summary")
    public ResponseEntity<SettlementSummaryResponse> getSettlementSummary(
            @PathVariable Long settlementId,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(allocationService.getSettlementSummary(settlementId, user));
    }
}
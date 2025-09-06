package goorm.hackathon.pizza.controller;

import goorm.hackathon.pizza.dto.request.*;
import goorm.hackathon.pizza.dto.response.*;
import goorm.hackathon.pizza.entity.User;
import goorm.hackathon.pizza.service.SettlementService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/settlements")
public class SettlementController {
    private final SettlementService settlementService;

    /**
     * 1단계: 임시 정산 생성 API
     */
    @PostMapping("/initiate")
    public ResponseEntity<SettlementResponse> createInitialSettlement(@AuthenticationPrincipal User user) {
        SettlementResponse response = settlementService.createInitialSettlement(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 2-1단계: 정산 제목 수정 API
     */
    @PatchMapping("/{settlementId}/title")
    public ResponseEntity<SettlementResponse> updateTitle(
            @PathVariable Long settlementId,
            @RequestBody UpdateTitleRequestDto request,
            @AuthenticationPrincipal User user) throws AccessDeniedException {
        SettlementResponse response = settlementService.updateTitle(settlementId, request, user);
        return ResponseEntity.ok(response);
    }

    /**
     * 3단계: 정산 참여 인원 설정 API
     */
    @PatchMapping("/{settlementId}/limit")
    public ResponseEntity<SettlementResponse> setParticipantLimit(
            @PathVariable Long settlementId,
            @RequestBody SetLimitRequestDto request,
            @AuthenticationPrincipal User user) throws AccessDeniedException {
        SettlementResponse response = settlementService.setParticipantLimit(settlementId, request, user);
        return ResponseEntity.ok(response);
    }



    @PostMapping("/{settlementId}/items")
    public ResponseEntity<List<ItemResponseDto>> addSettlementItems(
            @PathVariable Long settlementId,
            @RequestBody List<ItemRequestDto> itemDtos,
            @AuthenticationPrincipal User user) {

        List<ItemResponseDto> newItems = settlementService.addSettlementItems(settlementId, itemDtos);
        return ResponseEntity.status(HttpStatus.CREATED).body(newItems);
    }

    @PostMapping("/manual")
    public ResponseEntity<SettlementResponse> createSettlementWithItems(
            @RequestBody CreateSettlementRequestDto request,
            @AuthenticationPrincipal User user) {

        SettlementResponse response = settlementService.createSettlementWithItems(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 특정 정산의 모든 품목 목록을 조회하는 API
     */
    @GetMapping("/{settlementId}/items")
    public ResponseEntity<List<ItemInfoResponse>> getSettlementItems(@PathVariable Long settlementId) {
        List<ItemInfoResponse> itemList = settlementService.getSettlementItemList(settlementId);
        return ResponseEntity.ok(itemList);
    }


    /**
     * 특정 정산의 특정 품목을 수정하는 API
     */
    @PutMapping("/{settlementId}/items/{itemId}")
    public ResponseEntity<ItemResponseDto> updateSettlementItem(
            @PathVariable Long settlementId,
            @PathVariable Long itemId,
            @RequestBody ItemUpdateRequestDto requestDto) {

        ItemResponseDto updatedItem = settlementService.updateSettlementItem(settlementId, itemId, requestDto);
        return ResponseEntity.ok(updatedItem);
    }

    @DeleteMapping("/{settlementId}/items/{itemId}")
    public ResponseEntity<Void> deleteSettlementItem(
            @PathVariable Long settlementId,
            @PathVariable Long itemId) {

        settlementService.deleteSettlementItem(settlementId, itemId);
        return ResponseEntity.noContent().build(); // 204 No Content 반환
    }

    @PostMapping("/join")
    public ResponseEntity<?> joinSettlement(
            @RequestBody JoinRequestDto request,
            @AuthenticationPrincipal User user) {

        try {
            JoinSettlementResponse response = settlementService.verifyAndJoinSettlement(request.getCode(), user);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException | IllegalStateException e) {
            // 검증 실패 시 에러 메시지를 담아 409 Conflict 또는 404 Not Found 상태로 응답
            ErrorResponse errorBody = new ErrorResponse(HttpStatus.CONFLICT.value(), e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorBody);
        }
    }
}

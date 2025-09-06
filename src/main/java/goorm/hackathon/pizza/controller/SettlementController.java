package goorm.hackathon.pizza.controller;

import goorm.hackathon.pizza.dto.request.CreateSettlementRequestDto;
import goorm.hackathon.pizza.dto.request.ItemRequestDto;
import goorm.hackathon.pizza.dto.request.ItemUpdateRequestDto;
import goorm.hackathon.pizza.dto.request.SettlementCreationRequest;
import goorm.hackathon.pizza.dto.response.ItemInfoResponse;
import goorm.hackathon.pizza.dto.response.ItemResponseDto;
import goorm.hackathon.pizza.dto.response.SettlementResponse;
import goorm.hackathon.pizza.entity.User;
import goorm.hackathon.pizza.service.SettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/settlements")
public class SettlementController {
    private final SettlementService settlementService;

    @PostMapping("/create")
    public ResponseEntity<SettlementResponse> createSettlement(
            @RequestBody SettlementCreationRequest req,
            @AuthenticationPrincipal User user) {
        SettlementResponse response = settlementService.createSettlementWithParticipants(user, req.getParticipantLimit(), req.getTitle());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/temp")
    public ResponseEntity<SettlementResponse> createTempSettlement(
            @RequestParam("receiptId") Long receiptId,
            @AuthenticationPrincipal User user) {
        SettlementResponse response = settlementService.createTempSettlement(receiptId, user);
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
}

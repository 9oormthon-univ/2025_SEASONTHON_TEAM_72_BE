package goorm.hackathon.pizza.controller;

import goorm.hackathon.pizza.dto.response.ReceiptUploadResponse;
import goorm.hackathon.pizza.entity.User;
import goorm.hackathon.pizza.service.ReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/receipts")
public class ReceiptController {
    private final ReceiptService receiptService;

    @PostMapping("/upload/{settlementId}")
    public ResponseEntity<ReceiptUploadResponse> uploadReceipt(
            @RequestParam("image") MultipartFile image,
            @PathVariable Long settlementId, // settlementId를 경로 변수로 받음
            @AuthenticationPrincipal User user) {

        ReceiptUploadResponse response = receiptService.uploadReceipt(image, user, settlementId);
        return ResponseEntity.ok(response);
    }
}

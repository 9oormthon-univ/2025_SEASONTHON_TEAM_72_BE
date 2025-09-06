package goorm.hackathon.pizza.controller;

import goorm.hackathon.pizza.dto.response.ReceiptUploadResponse;
import goorm.hackathon.pizza.entity.User;
import goorm.hackathon.pizza.service.ReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/receipts")
public class ReceiptController {
    private final ReceiptService receiptService;

    @PostMapping("/upload")
    public ResponseEntity<ReceiptUploadResponse> uploadReceipt(
            @RequestParam("image") MultipartFile image,
            @AuthenticationPrincipal User user) {
        ReceiptUploadResponse response = receiptService.uploadReceipt(image, user);
        return ResponseEntity.ok(response);
    }
}

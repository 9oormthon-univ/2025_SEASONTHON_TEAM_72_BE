package goorm.hackathon.pizza.service;

import goorm.hackathon.pizza.dto.response.ReceiptUploadResponse;
import goorm.hackathon.pizza.entity.Receipt;
import goorm.hackathon.pizza.entity.User;
import goorm.hackathon.pizza.repository.ReceiptRepository;
import goorm.hackathon.pizza.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ReceiptServiceImpl implements ReceiptService {

    private final ReceiptRepository receiptRepository;
    private final SettlementRepository settlementRepository;
    private final CloudinaryService cloudinaryService;

    // 영수증 이미지 저장
    @Transactional
    @Override
    public ReceiptUploadResponse uploadReceipt(MultipartFile file, User user) {
        String imageUrl;
        imageUrl = cloudinaryService.uploadImage(file);

        Receipt newReceipt = Receipt.builder()
                .imageUrl(imageUrl)
                .createdBy(user)
                .totalAmount(BigDecimal.ZERO)
                .build();

        Receipt savedReceipt = receiptRepository.save(newReceipt);

        return ReceiptUploadResponse.builder()
                .receiptId(savedReceipt.getId())
                .imgUrl(savedReceipt.getImageUrl())
                .OCR(null)
                .build();
    }

    // 영수증 텍스트 데이터 저장
    @Override
    public void saveReceiptText() {

    }

    // 영수증 정보 불러오기
    @Override
    public void loadReceipt() {

    }

    // 영수증 정보 수정하기
    @Override
    public void updateReceipt() {

    }

    // 영수증 이미지 삭제하기
    @Override
    public void deleteReceipt() {

    }

}

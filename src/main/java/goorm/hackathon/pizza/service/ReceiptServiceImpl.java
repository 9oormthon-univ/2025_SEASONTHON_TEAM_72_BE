package goorm.hackathon.pizza.service;

import goorm.hackathon.pizza.dto.response.ReceiptUploadResponse;
import goorm.hackathon.pizza.entity.Receipt;
import goorm.hackathon.pizza.entity.Settlement;
import goorm.hackathon.pizza.entity.User;
import goorm.hackathon.pizza.repository.ReceiptRepository;
import goorm.hackathon.pizza.repository.SettlementRepository;
import jakarta.persistence.EntityNotFoundException;
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
    @Override
    @Transactional
    public ReceiptUploadResponse uploadReceipt(MultipartFile file, User user, Long settlementId) {
        // 1. 이미지를 Cloudinary에 업로드하고 URL을 받아옵니다.
        String imageUrl;
        imageUrl = cloudinaryService.uploadImage(file);

        // 2. settlementId로 정산 엔티티를 조회합니다.
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new EntityNotFoundException("해당 정산을 찾을 수 없습니다."));

        // 3. Receipt 객체를 생성하고 정산과 연결
        Receipt newReceipt = Receipt.builder()
                .imageUrl(imageUrl)
                .createdBy(user)
                .settlement(settlement) // 조회한 정산과 연결
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

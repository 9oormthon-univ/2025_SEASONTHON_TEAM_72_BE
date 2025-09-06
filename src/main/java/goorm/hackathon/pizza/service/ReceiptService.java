package goorm.hackathon.pizza.service;

import goorm.hackathon.pizza.dto.response.ReceiptUploadResponse;
import goorm.hackathon.pizza.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface ReceiptService {
    // 영수증 이미지 저장
    ReceiptUploadResponse uploadReceipt(MultipartFile file, User user, Long settlementId);

    // 영수증 텍스트 데이터 저장
    void saveReceiptText();

    // 영수증 정보 불러오기
    void loadReceipt();

    // 영수증 정보 수정하기
    void updateReceipt();

    // 영수증 이미지 삭제하기
    void deleteReceipt();
}

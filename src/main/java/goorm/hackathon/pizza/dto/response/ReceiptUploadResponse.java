package goorm.hackathon.pizza.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReceiptUploadResponse {
    private Long receiptId;
    private String imgUrl;
    private String OCR;
}

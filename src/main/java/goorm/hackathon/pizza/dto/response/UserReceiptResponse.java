package goorm.hackathon.pizza.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class UserReceiptResponse {
    private Long userId;
    private String userNickname;
    private BigDecimal totalDueAmount; // 유저가 내야 할 총액
    private List<UserReceiptItemDetail> allocatedItems; // 유저가 참여한 품목 목록
}
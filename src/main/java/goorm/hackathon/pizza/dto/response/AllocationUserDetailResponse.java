package goorm.hackathon.pizza.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;

@Getter
@Builder
public class AllocationUserDetailResponse {
    private Long userId;
    private String userNickname;
    private BigDecimal quantity; // 해당 유저가 선택한 수량
    private BigDecimal amount;   // 해당 유저가 내야 할 금액
}
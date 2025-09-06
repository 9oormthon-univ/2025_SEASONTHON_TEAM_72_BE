package goorm.hackathon.pizza.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;

@Getter
@Builder
public class UserReceiptItemDetail {
    private Long itemId;
    private String itemName;
    private BigDecimal myQuantity;   // 내가 선택한 수량
    private BigDecimal myDueAmount;  // 내가 내야 할 금액
}
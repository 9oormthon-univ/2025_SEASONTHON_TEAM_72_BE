package goorm.hackathon.pizza.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class AllocateItemRequest {
    @Min(value = 1, message = "수량은 1 이상이어야 합니다.")
    private BigDecimal quantity; // 사용자가 선택한 수량
}
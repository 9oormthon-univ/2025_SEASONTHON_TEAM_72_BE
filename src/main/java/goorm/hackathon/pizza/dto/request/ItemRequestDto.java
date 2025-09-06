package goorm.hackathon.pizza.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class ItemRequestDto {
    private String name;

    @NotNull(message = "품목 가격은 필수입니다.") // 이 어노테이션 추가
    private BigDecimal totalPrice;

    @NotNull(message = "품목 수량은 필수입니다.") // 이 어노테이션 추가
    private BigDecimal totalQuantity;
}
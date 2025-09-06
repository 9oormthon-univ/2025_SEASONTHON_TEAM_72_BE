package goorm.hackathon.pizza.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ItemResponseDto {
    private Long itemId;
    private String name;
    private BigDecimal totalPrice;
    private BigDecimal totalQuantity;
}

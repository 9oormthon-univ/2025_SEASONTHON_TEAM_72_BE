package goorm.hackathon.pizza.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemResponseDto {
    private Long itemId;
    private String name;
    private BigDecimal totalPrice;
    private BigDecimal totalQuantity;
}

package goorm.hackathon.pizza.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ItemUpdateRequestDto {
    private String name;
    private BigDecimal totalPrice;
    private BigDecimal totalQuantity;
}
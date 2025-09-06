package goorm.hackathon.pizza.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemRequestDto {
    private String name;
    private BigDecimal totalPrice;
    private BigDecimal totalQuantity;
}
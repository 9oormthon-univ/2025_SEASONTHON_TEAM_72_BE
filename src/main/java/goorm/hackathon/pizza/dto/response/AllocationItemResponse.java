package goorm.hackathon.pizza.dto.response;

import goorm.hackathon.pizza.entity.Enum.AllocationStatus;
import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AllocationItemResponse {
    private Long itemId;
    private String name;
    private BigDecimal totalPrice;
    private BigDecimal totalQuantity;
    private AllocationStatus status;
}

// PaymentResponse.java
package goorm.hackathon.pizza.dto.response.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PaymentResponse {
    private Long settlementId;
    private Long userId;
    private boolean paid;
    private String message;
}

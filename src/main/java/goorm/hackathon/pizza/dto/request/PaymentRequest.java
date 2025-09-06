// PaymentRequest.java
package goorm.hackathon.pizza.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class PaymentRequest {
    @JsonProperty("user_id")
    private Long userId;
}

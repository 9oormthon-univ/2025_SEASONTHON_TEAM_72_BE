package goorm.hackathon.pizza.dto.response;

import goorm.hackathon.pizza.entity.Enum.SettlementStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SettlementResponse {
    private Long settlementId;
    private String title;
    private SettlementStatus status;
}

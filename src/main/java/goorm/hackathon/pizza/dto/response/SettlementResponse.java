package goorm.hackathon.pizza.dto.response;

import goorm.hackathon.pizza.entity.Enum.SettlementStatus;
import goorm.hackathon.pizza.entity.Settlement;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SettlementResponse {
    private Long settlementId;
    private String title;
    private SettlementStatus status;

    public static SettlementResponse from(Settlement settlement) {
        return SettlementResponse.builder()
                .settlementId(settlement.getId())
                .title(settlement.getTitle())
                .status(settlement.getStatus())
                .build();
    }
}

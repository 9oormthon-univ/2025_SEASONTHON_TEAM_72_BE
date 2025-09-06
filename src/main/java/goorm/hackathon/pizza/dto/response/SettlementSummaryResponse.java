package goorm.hackathon.pizza.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class SettlementSummaryResponse {
    private Long settlementId;
    private String title;
    private BigDecimal totalAmount;
    private List<ParticipantSummaryDto> participants;
}
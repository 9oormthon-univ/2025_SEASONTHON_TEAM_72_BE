package goorm.hackathon.pizza.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;

@Getter
@Builder
public class ParticipantSummaryDto {
    private Long userId;
    private String userNickname;
    private BigDecimal dueAmount;
    private Boolean isPaid;
}
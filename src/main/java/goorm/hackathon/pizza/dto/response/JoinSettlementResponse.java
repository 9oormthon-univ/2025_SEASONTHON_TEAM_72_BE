package goorm.hackathon.pizza.dto.response;

import goorm.hackathon.pizza.entity.Enum.ParticipantRole;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class JoinSettlementResponse {
    private Long settlementId;
    private String title;
    private String message;
    private Long participationId;
    private ParticipantRole role;
    private LocalDateTime joinedAt;
}

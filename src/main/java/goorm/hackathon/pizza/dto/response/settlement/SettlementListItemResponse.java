package goorm.hackathon.pizza.dto.response.settlement;

import goorm.hackathon.pizza.entity.Enum.ParticipantRole;
import goorm.hackathon.pizza.entity.Enum.SettlementStatus;

import java.time.LocalDateTime;

public record SettlementListItemResponse(
        Long settlementId,
        String title,
        SettlementStatus status,
        LocalDateTime createdAt,
        ParticipantRole role
) {}

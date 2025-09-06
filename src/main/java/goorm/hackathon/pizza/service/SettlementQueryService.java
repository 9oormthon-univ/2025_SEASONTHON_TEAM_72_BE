package goorm.hackathon.pizza.service;

import goorm.hackathon.pizza.dto.response.settlement.SettlementListItemResponse;
import goorm.hackathon.pizza.entity.Settlement;
import goorm.hackathon.pizza.entity.Enum.ParticipantRole;
import goorm.hackathon.pizza.entity.Enum.SettlementStatus;
import goorm.hackathon.pizza.repository.ParticipationRepository;
import goorm.hackathon.pizza.repository.SettlementRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.List;

@Service
public class SettlementQueryService {

    private final SettlementRepository settlements;
    private final ParticipationRepository participation;

    public SettlementQueryService(SettlementRepository settlements, ParticipationRepository participation) {
        this.settlements = settlements;
        this.participation = participation;
    }

    public List<SettlementListItemResponse> getOngoing(Long userId) {
        var statuses = EnumSet.of(
                SettlementStatus.IN_PROGRESS,
                SettlementStatus.AWAITING_DEPOSIT,
                SettlementStatus.NEEDS_ATTENTION
        );
        return queryAsList(userId, statuses);
    }

    public List<SettlementListItemResponse> getCompleted(Long userId) {
        var statuses = EnumSet.of(SettlementStatus.DONE);
        return queryAsList(userId, statuses);
    }

    private List<SettlementListItemResponse> queryAsList(
            Long userId, EnumSet<SettlementStatus> statuses
    ) {
        var sort = Sort.by(Sort.Direction.DESC, "createdAt");
        List<Settlement> list = settlements.findAllVisibleToUser(userId, statuses, sort);

        return list.stream()
                .map(s -> new SettlementListItemResponse(
                        s.getId(),
                        s.getTitle(),
                        s.getStatus(),
                        s.getCreatedAt(),
                        participation.findRole(s.getId(), userId)
                                .orElseGet(() -> s.getOwner().getUserId().equals(userId)
                                        ? ParticipantRole.OWNER
                                        : ParticipantRole.MEMBER)
                ))
                .toList();
    }
}

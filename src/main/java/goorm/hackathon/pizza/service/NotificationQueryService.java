package goorm.hackathon.pizza.service;

import goorm.hackathon.pizza.dto.response.notification.NotificationResponse;
import goorm.hackathon.pizza.entity.Enum.LinkType;
import goorm.hackathon.pizza.entity.Enum.ParticipantRole;
import goorm.hackathon.pizza.entity.Notification;
import goorm.hackathon.pizza.repository.NotificationRepository;
import goorm.hackathon.pizza.repository.ParticipationRepository;
import goorm.hackathon.pizza.repository.SettlementRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationQueryService {

    private final NotificationRepository notifications;
    private final SettlementRepository settlements;
    private final ParticipationRepository participation;

    public NotificationQueryService(
            NotificationRepository notifications,
            SettlementRepository settlements,
            ParticipationRepository participation
    ) {
        this.notifications = notifications;
        this.settlements = settlements;
        this.participation = participation;
    }

  //  알림 전체 목록: id, type, message, read, status(정산), role(정산 내 역할)
    public List<NotificationResponse> getAll(Long userId) {
        var sort = Sort.by(Sort.Direction.DESC, "createdAt");
        List<Notification> list = notifications.findByRecipient_UserId(userId, sort);

        return list.stream().map(n -> {
            String status = null;
            String role   = null;

            if (n.getLinkType() == LinkType.SETTLEMENT && n.getLinkId() != null) {
                var sOpt = settlements.findById(n.getLinkId());
                if (sOpt.isPresent()) {
                    var s = sOpt.get();
                    status = s.getStatus().name();

                    // OWNER 우선 판별, 아니면 참여 테이블에서 역할 조회
                    if (s.getOwner().getUserId().equals(userId)) {
                        role = ParticipantRole.OWNER.name();
                    } else {
                        role = participation.findRole(s.getId(), userId)
                                .map(Enum::name)
                                .orElse(null);
                    }
                }
            }

            return new NotificationResponse(
                    n.getId(),
                    n.getType(),
                    n.getMessage(),
                    n.isRead(),
                    status,
                    role,
                    n.getCreatedAt()
            );
        }).toList();
    }
}

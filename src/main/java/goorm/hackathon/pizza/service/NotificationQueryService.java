package goorm.hackathon.pizza.service;

import goorm.hackathon.pizza.dto.response.notification.NotificationResponse;
import goorm.hackathon.pizza.entity.Enum.NotificationType;
import goorm.hackathon.pizza.entity.Enum.LinkType;
import goorm.hackathon.pizza.entity.Enum.ParticipantRole;
import goorm.hackathon.pizza.entity.Notification;
import goorm.hackathon.pizza.repository.NotificationRepository;
import goorm.hackathon.pizza.repository.ParticipationRepository;
import goorm.hackathon.pizza.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class NotificationQueryService {

    private final NotificationRepository notificationRepository;
    private final ParticipationRepository participationRepository;
    private final SettlementRepository settlementRepository;
    //전체 알림 최신순으로 반환
    public List<NotificationResponse> findMyNotifications(Long userId) {
        return notificationRepository.findByRecipient_UserIdOrderByCreatedAtDescIdDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    //엔티티 -> 응답 DTO (message: 저장 우선, 없으면 타입 기반 기본 문구)
    private NotificationResponse toResponse(Notification n) {
        String message = (n.getMessage() != null && !n.getMessage().isBlank())
                ? n.getMessage()
                : resolveMessage(n.getType());

        // 기존 응답 호환: status 자리에 linkType 문자열 유지
        String status = (n.getLinkType() == null) ? null : n.getLinkType().name();

        //role 계산
        String role = resolveRole(n);

        return new NotificationResponse(
                n.getId(),
                n.getType(),
                message,
                n.isRead(),
                status,
                role,
                n.getCreatedAt()
        );
    }

    /** 기본 문구 매핑 */
    private String resolveMessage(NotificationType type) {
        return switch (type) {
            case SETTLEMENT_COMPLETED -> "정산이 완료되었습니다.";
            case DEPOSIT_REQUEST -> "입금 요청이 도착했습니다.";
            case DEPOSIT_CONFIRMED -> "입금이 확인되었습니다.";
            case SETTLEMENT_COMPLETE_REQUEST -> "정산을 완료해 주세요.";
            case DEPOSIT_CANCELED -> "입금이 취소 처리되었습니다.";
        };
    }

    /** 알림에 연결된 정산 문맥에서 recipient의 역할을 계산 */
    private String resolveRole(Notification n) {
        if (n.getLinkType() != LinkType.SETTLEMENT || n.getLinkId() == null) {
            return null; // 정산문맥 없으면 역할 없음
        }

        Long settlementId = n.getLinkId();
        Long userId = n.getRecipient().getUserId();

        // 1) 참여자 역할 먼저 조회
        var roleOpt = participationRepository.findRole(settlementId, userId);
        if (roleOpt.isPresent()) {
            ParticipantRole r = roleOpt.get();
            return (r == null) ? null : r.name(); // MEMBER / MANAGER 등
        }

        // 2) 참여자가 아니면 owner 인지 확인 → OWNER
        Long ownerId = settlementRepository.findOwnerUserId(settlementId);
        if (ownerId != null && Objects.equals(ownerId, userId)) {
            return "OWNER";
        }

        // 3) 둘 다 아니면 null
        return null;
    }
}

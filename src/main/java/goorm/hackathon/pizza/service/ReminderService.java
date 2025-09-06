// ReminderService.java
package goorm.hackathon.pizza.service;

import goorm.hackathon.pizza.dto.response.notification.ReminderResponse;
import goorm.hackathon.pizza.entity.Enum.LinkType;
import goorm.hackathon.pizza.entity.Enum.NotificationType;
import goorm.hackathon.pizza.entity.Notification;
import goorm.hackathon.pizza.entity.Settlement;
import goorm.hackathon.pizza.entity.User;
import goorm.hackathon.pizza.repository.NotificationRepository;
import goorm.hackathon.pizza.repository.ParticipationRepository;
import goorm.hackathon.pizza.repository.SettlementRepository;
import goorm.hackathon.pizza.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReminderService {

    private final SettlementRepository settlementRepository;
    private final ParticipationRepository participationRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    /**
     * 총괄자가 특정 참여자에게 입금 독촉을 전송
     */
    @Transactional
    public ReminderResponse remindOne(Long settlementId, Long targetUserId, Long requesterId) {

        // 1) 정산 조회 + 총괄자 권한 검증
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new EntityNotFoundException("정산을 찾을 수 없습니다."));

        Long ownerId = settlement.getOwner() != null ? settlement.getOwner().getUserId() : null;
        if (ownerId == null || !ownerId.equals(requesterId)) {
            throw new AccessDeniedException("정산의 총괄자만 독촉할 수 있습니다.");
        }

        // 2) 대상자가 해당 정산의 참여자인지 확인
        var participation = participationRepository
                .findBySettlementIdAndUserId(settlementId, targetUserId)
                .orElseThrow(() ->
                        new EntityNotFoundException("대상 사용자가 해당 정산의 참여자가 아닙니다."));

        // 3) 이미 입금 완료면 독촉 불가
        if (participation.isPaid()) {
            throw new IllegalStateException("해당 사용자는 이미 입금 완료 상태입니다.");
        }

        // 4) 메시지 생성: [정산제목] 총괄자닉네임님이 입금 요청을 보냈습니다.
        String title = settlement.getTitle() != null ? settlement.getTitle() : "정산";
        String ownerNickname = settlement.getOwner() != null
                ? settlement.getOwner().getNickname()
                : userRepository.findById(requesterId).map(User::getNickname).orElse("총괄자");

        String message = String.format("[%s] %s님이 입금 요청을 보냈습니다.", title, ownerNickname);

        // 5) 수신자 로드 후 알림 저장
        User recipient = userRepository.findById(targetUserId)
                .orElseThrow(() -> new EntityNotFoundException("대상 사용자 정보를 찾을 수 없습니다."));

        Notification notification = Notification.builder()
                .recipient(recipient)
                .type(NotificationType.DEPOSIT_REQUEST)
                .message(message)
                .linkType(LinkType.SETTLEMENT)
                .linkId(settlementId)
                .isRead(false)
                .build();

        notificationRepository.save(notification);

        // 6) 응답 DTO
        return ReminderResponse.of(notification);
    }
}

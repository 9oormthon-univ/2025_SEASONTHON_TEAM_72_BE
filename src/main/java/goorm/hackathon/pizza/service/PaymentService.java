// src/main/java/goorm/hackathon/pizza/service/PaymentService.java
package goorm.hackathon.pizza.service;

import goorm.hackathon.pizza.dto.response.notification.NotificationSimpleResponse;
import goorm.hackathon.pizza.entity.Enum.LinkType;
import goorm.hackathon.pizza.entity.Enum.NotificationType;
import goorm.hackathon.pizza.entity.Notification;
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
@Transactional
public class PaymentService {

    private final SettlementRepository settlementRepository;
    private final ParticipationRepository participationRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    /**
     * (총괄자 전용) 특정 사용자(targetUserId)의 입금을 '완료' 처리
     * 응답: 독촉하기와 동일 포맷의 NotificationSimpleResponse
     */
    public NotificationSimpleResponse confirmDeposit(Long settlementId, Long targetUserId, Long requesterId) {
        // 권한 체크
        Long ownerId = settlementRepository.findOwnerId(settlementId)
                .orElseThrow(() -> new EntityNotFoundException("정산을 찾을 수 없습니다."));
        if (!ownerId.equals(requesterId)) {
            throw new AccessDeniedException("총괄자만 처리 가능합니다.");
        }

        // 참여자 확인
        var p = participationRepository.findBySettlementIdAndUserId(settlementId, targetUserId)
                .orElseThrow(() -> new EntityNotFoundException("참여자를 찾을 수 없습니다."));
        if (p.isPaid()) {
            throw new IllegalStateException("이미 입금 완료 상태입니다.");
        }

        // 상태 업데이트
        int updated = participationRepository.markPaid(settlementId, targetUserId);
        if (updated == 0) {
            throw new IllegalStateException("입금 완료 처리에 실패했습니다.");
        }

        // 메시지 구성
        String title = settlementRepository.findTitle(settlementId).orElse("정산");
        var target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new EntityNotFoundException("대상자 없음"));
        var requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new EntityNotFoundException("요청자 없음"));

        String message = "[" + title + "] " + target.getNickname() + "님이 입금하셨습니다.";

        // 알림(수신자: 총괄자)
        Notification n = Notification.builder()
                .recipient(requester)
                .type(NotificationType.DEPOSIT_CONFIRMED)
                .message(message)
                .linkType(LinkType.SETTLEMENT)
                .linkId(settlementId)
                .isRead(false)
                .build();

        notificationRepository.save(n);
        return NotificationSimpleResponse.of(n);
    }

    /**
     * (총괄자 전용) 특정 사용자(targetUserId)의 입금을 '취소' 처리
     * 응답: 독촉하기와 동일 포맷의 NotificationSimpleResponse
     */
    public NotificationSimpleResponse cancelDeposit(Long settlementId, Long targetUserId, Long requesterId) {
        // 권한 체크
        Long ownerId = settlementRepository.findOwnerId(settlementId)
                .orElseThrow(() -> new EntityNotFoundException("정산을 찾을 수 없습니다."));
        if (!ownerId.equals(requesterId)) {
            throw new AccessDeniedException("총괄자만 처리 가능합니다.");
        }

        var p = participationRepository.findBySettlementIdAndUserId(settlementId, targetUserId)
                .orElseThrow(() -> new EntityNotFoundException("참여자를 찾을 수 없습니다."));
        if (!p.isPaid()) {
            throw new IllegalStateException("입금 완료 상태가 아니라 취소할 수 없습니다.");
        }

        int updated = participationRepository.cancelPaid(settlementId, targetUserId);
        if (updated == 0) {
            throw new IllegalStateException("입금 취소 처리에 실패했습니다.");
        }

        // 메시지 구성
        String title = settlementRepository.findTitle(settlementId).orElse("정산");
        var target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new EntityNotFoundException("대상자 없음"));

        String message = "[" + title + "] 입금이 취소 처리되었습니다.";

        // 알림(수신자: 대상 사용자)
        Notification n = Notification.builder()
                .recipient(target)
                .type(NotificationType.DEPOSIT_CANCELED)
                .message(message)
                .linkType(LinkType.SETTLEMENT)
                .linkId(settlementId)
                .isRead(false)
                .build();

        notificationRepository.save(n);
        return NotificationSimpleResponse.of(n);
    }
}

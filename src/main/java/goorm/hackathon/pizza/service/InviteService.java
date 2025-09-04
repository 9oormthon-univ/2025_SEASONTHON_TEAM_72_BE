package goorm.hackathon.pizza.service;

import goorm.hackathon.pizza.entity.InviteEntity;
import goorm.hackathon.pizza.repository.InviteRepository;
import goorm.hackathon.pizza.util.InviteCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InviteService {

    private final InviteRepository inviteRepository;

    /** 활성 초대가 있으면 그대로 반환, 없으면 새로 생성 */
    @Transactional
    public InviteEntity createOrFetch(Long settlementId, Long userId) {
        return inviteRepository.findFirstBySettlementIdAndIsActiveTrue(settlementId)
                .orElseGet(() -> createNew(settlementId, userId));
    }

    private InviteEntity createNew(Long settlementId, Long userId) {
        final LocalDateTime expiresAt = LocalDateTime.now().plusDays(7);
        for (int i = 0; i < 6; i++) {
            String code = InviteCodeGenerator.generate(8); // 대문자+숫자
            InviteEntity entity = InviteEntity.builder()
                    .settlementId(settlementId)
                    .code(code)            // @PrePersist에서 UPPERCASE 보정
                    .isActive(true)
                    .expiresAt(expiresAt)
                    .createdBy(userId)
                    .build();
            try {
                return inviteRepository.saveAndFlush(entity);
            } catch (DataIntegrityViolationException dup) {
                // UNIQUE(code) 충돌 → 재시도
            }
        }
        throw new IllegalStateException("Failed to generate unique invite code");
    }

    /** 코드 조회(대소문자 허용) */
    @Transactional(readOnly = true)
    public InviteEntity getActiveByCode(String rawCode) {
        String code = InviteCodeGenerator.normalize(rawCode);
        return inviteRepository.findByCodeAndIsActiveTrue(code)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or inactive invite code"));
    }
}

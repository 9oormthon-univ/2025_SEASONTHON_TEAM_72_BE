package goorm.hackathon.pizza.repository;

import goorm.hackathon.pizza.entity.Enum.SettlementStatus;
import goorm.hackathon.pizza.entity.Settlement;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {

    // 사용자가 볼 수 있는 정산 목록 (상태 + 소유자/참여자)
    @Query("""
        SELECT s
          FROM Settlement s
         WHERE s.status IN :statuses
           AND (
                s.owner.userId = :userId
             OR s.id IN (
                    SELECT p.settlement.id
                      FROM Participation p
                     WHERE p.user.userId = :userId
                )
           )
         ORDER BY s.createdAt DESC
    """)
    List<Settlement> findAllVisibleToUser(
            @Param("userId") Long userId,
            @Param("statuses") Collection<SettlementStatus> statuses,
            Sort sort
    );

    // 총괄자 권한 확인
    @Query("""
        select (count(s) > 0)
          from Settlement s
         where s.id = :sid
           and s.owner.userId = :ownerId
    """)
    boolean existsByIdAndOwner(@Param("sid") Long settlementId,
                               @Param("ownerId") Long ownerId);

    // ====== 표준: 총괄자 userId 조회 ======
    @Query("""
        select s.owner.userId
          from Settlement s
         where s.id = :sid
    """)
    Long findOwnerUserId(@Param("sid") Long settlementId);

    // (호환) 기존 Optional<Long> 반환 메서드
    default Optional<Long> findOwnerId(Long settlementId) {
        return Optional.ofNullable(findOwnerUserId(settlementId));
    }

    // ====== 정산 제목 조회 ======
    @Query("""
        select s.title
          from Settlement s
         where s.id = :sid
    """)
    String findTitlePlain(@Param("sid") Long settlementId);

    // (호환) Optional<String> 버전
    default Optional<String> findTitle(Long settlementId) {
        return Optional.ofNullable(findTitlePlain(settlementId));
    }

    // ====== 총괄자 닉네임 조회 ======
    @Query("""
        select s.owner.nickname
          from Settlement s
         where s.id = :sid
    """)
    String findOwnerNicknamePlain(@Param("sid") Long settlementId);

    // (호환) Optional<String> 버전
    default Optional<String> findOwnerNickname(@Param("sid") Long settlementId) {
        return Optional.ofNullable(findOwnerNicknamePlain(settlementId));
    }
}

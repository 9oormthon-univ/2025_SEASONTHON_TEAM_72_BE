package goorm.hackathon.pizza.repository;

import goorm.hackathon.pizza.entity.Enum.ParticipantRole;
import goorm.hackathon.pizza.entity.Participation;
import goorm.hackathon.pizza.entity.Settlement;
import goorm.hackathon.pizza.entity.User;
import goorm.hackathon.pizza.repository.rows.OverallItemRow;
import goorm.hackathon.pizza.repository.rows.UserItemRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    // === 기존 ===
    boolean existsBySettlementAndUser(Settlement settlement, User user);

    // === ID 기반 파생 쿼리 ===
    boolean existsBySettlement_IdAndUser_UserId(Long settlementId, Long userId);

    Optional<Participation> findBySettlement_IdAndUser_UserId(Long settlementId, Long userId);

    // 호환용 커스텀 이름
    @Query("""
        select p
          from Participation p
         where p.settlement.id = :sid
           and p.user.userId   = :uid
    """)
    Optional<Participation> findBySettlementIdAndUserId(@Param("sid") Long settlementId,
                                                        @Param("uid") Long userId);

    // 역할 조회
    @Query("""
        select p.role
          from Participation p
         where p.settlement.id = :sid
           and p.user.userId    = :uid
    """)
    Optional<ParticipantRole> findRole(@Param("sid") Long settlementId,
                                       @Param("uid") Long userId);

    // 사용자별 품목 행
    @Query("""
        select new goorm.hackathon.pizza.repository.rows.UserItemRow(
            p.user.userId,
            p.userNickname,
            p.isPaid,
            i.name,
            a.quantity,
            i.totalPrice,
            i.totalQuantity
        )
        from Allocation a
          join a.participation p
          join a.item i
        where p.settlement.id = :sid
        order by p.userNickname, i.name
    """)
    List<UserItemRow> findUserItemRows(@Param("sid") Long settlementId);

    // 전체 품목 합산 행
    @Query("""
        select new goorm.hackathon.pizza.repository.rows.OverallItemRow(
            i.name,
            sum(a.quantity),
            i.totalPrice,
            i.totalQuantity
        )
        from Allocation a
          join a.participation p
          join a.item i
        where p.settlement.id = :sid
        group by i.id, i.name, i.totalPrice, i.totalQuantity
        order by i.name
    """)
    List<OverallItemRow> findOverallItemRows(@Param("sid") Long settlementId);

    // 미입금 인원 수
    @Query("""
        select count(p)
          from Participation p
         where p.settlement.id = :sid
           and p.isPaid = false
    """)
    long countUnpaid(@Param("sid") Long settlementId);

    // 특정 유저의 입금 여부
    @Query("""
        select p.isPaid
          from Participation p
         where p.settlement.id = :sid
           and p.user.userId    = :uid
    """)
    Optional<Boolean> isPaid(@Param("sid") Long settlementId, @Param("uid") Long userId);

    // 정산 제목 (알림 메시지용 등)
    @Query("""
        select s.title
          from Settlement s
         where s.id = :sid
    """)
    String findSettlementTitle(@Param("sid") Long settlementId);

    // ====== 입금 상태 갱신 (엔티티 변경 없이 DB에서 직접 업데이트) ======

    // 입금 완료 처리
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update Participation p
           set p.isPaid = true,
               p.paidAt = CURRENT_TIMESTAMP
         where p.settlement.id = :sid
           and p.user.userId    = :uid
           and p.isPaid = false
    """)
    int markPaid(@Param("sid") Long settlementId,
                 @Param("uid") Long userId);

    // 입금 취소 처리
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update Participation p
           set p.isPaid     = false,
               p.paidAt     = null,
               p.paidAmount = null
         where p.settlement.id = :sid
           and p.user.userId    = :uid
           and p.isPaid = true
    """)
    int cancelPaid(@Param("sid") Long settlementId,
                   @Param("uid") Long userId);
}

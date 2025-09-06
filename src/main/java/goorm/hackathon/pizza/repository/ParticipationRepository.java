package goorm.hackathon.pizza.repository;

import goorm.hackathon.pizza.entity.Enum.ParticipantRole;
import goorm.hackathon.pizza.entity.Participation;
import goorm.hackathon.pizza.entity.Settlement;
import goorm.hackathon.pizza.entity.User;
import goorm.hackathon.pizza.repository.rows.OverallItemRow;
import goorm.hackathon.pizza.repository.rows.UserItemRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    //미 참여 중인지 확인 (Service에서 사용하는 메서드)
    boolean existsBySettlementAndUser(Settlement settlement, User user);

    // (원하면 PK로도 사용 가능: 불필요한 엔티티 로딩 피함)
    // boolean existsBySettlement_IdAndUser_UserId(Long settlementId, Long userId);

    @Query("""
        select p.role
          from Participation p
         where p.settlement.id = :sid
           and p.user.userId = :uid
    """)
    Optional<ParticipantRole> findRole(@Param("sid") Long settlementId,
                                       @Param("uid") Long userId);

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

    @Query("""
        select count(p)
          from Participation p
         where p.settlement.id = :sid
           and p.isPaid = false
    """)
    long countUnpaid(@Param("sid") Long settlementId);
}

package goorm.hackathon.pizza.repository;

import goorm.hackathon.pizza.entity.Enum.ParticipantRole;
import goorm.hackathon.pizza.entity.Participation;
import goorm.hackathon.pizza.repository.rows.OverallItemRow;
import goorm.hackathon.pizza.repository.rows.UserItemRow;
import goorm.hackathon.pizza.entity.Settlement;
import goorm.hackathon.pizza.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    // 특정 유저가 특정 정산에 이미 참여했는지 확인하기 위한 메서드
    boolean existsBySettlementAndUser(Settlement settlement, User user);
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




    // "전체" 품목별 수량 합 rows (단가/라인금액은 서비스에서 계산)
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

    // 미납 인원 수
    @Query("""
        select count(p)
          from Participation p
         where p.settlement.id = :sid
           and p.isPaid = false
    """)
    long countUnpaid(@Param("sid") Long settlementId);

}

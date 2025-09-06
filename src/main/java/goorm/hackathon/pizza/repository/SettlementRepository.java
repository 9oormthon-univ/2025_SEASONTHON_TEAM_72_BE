package goorm.hackathon.pizza.repository;

import goorm.hackathon.pizza.entity.Settlement;
import goorm.hackathon.pizza.entity.Enum.SettlementStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {

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

    //owner 유저 ID만 단건으로 조회 (Notification role 계산용) */
    @Query("""
        select s.owner.userId
          from Settlement s
         where s.id = :sid
    """)
    Long findOwnerUserId(@Param("sid") Long settlementId);
}

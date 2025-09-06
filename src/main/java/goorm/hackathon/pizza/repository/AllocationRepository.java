package goorm.hackathon.pizza.repository;

import goorm.hackathon.pizza.entity.Allocation;
import goorm.hackathon.pizza.entity.Item;
import goorm.hackathon.pizza.entity.Participation;
import goorm.hackathon.pizza.repository.rows.UserItemRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface AllocationRepository extends JpaRepository<Allocation, Long> {

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
    // 특정 참여자(Participation)와 특정 품목(Item)에 대한 분배 정보를 찾는 메서드
    Optional<Allocation> findByParticipationAndItem(Participation participation, Item item);
}

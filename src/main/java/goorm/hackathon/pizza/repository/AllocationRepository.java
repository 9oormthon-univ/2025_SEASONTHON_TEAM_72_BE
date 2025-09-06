// src/main/java/goorm/hackathon/pizza/repository/AllocationRepository.java
package goorm.hackathon.pizza.repository;

import goorm.hackathon.pizza.entity.Allocation;
import goorm.hackathon.pizza.repository.rows.UserItemRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}

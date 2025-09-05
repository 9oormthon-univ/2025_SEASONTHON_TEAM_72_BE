package goorm.hackathon.pizza.repository;

import goorm.hackathon.pizza.entity.Enum.ParticipantRole;
import goorm.hackathon.pizza.entity.Participation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    @Query("""
        select p.role
          from Participation p
         where p.settlement.id = :sid
           and p.user.userId = :uid
    """)
    Optional<ParticipantRole> findRole(@Param("sid") Long settlementId,
                                       @Param("uid") Long userId);
}

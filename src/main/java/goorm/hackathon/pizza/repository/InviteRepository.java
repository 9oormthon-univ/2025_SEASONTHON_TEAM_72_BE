package goorm.hackathon.pizza.repository;

import goorm.hackathon.pizza.entity.Invite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface InviteRepository extends JpaRepository<Invite, Long> {
    Optional<Invite> findByCodeAndIsActiveTrue(String code);

    Optional<Invite> findByCode(String code);
    Optional<Invite> findFirstBySettlementIdAndIsActiveTrue(Long settlementId);

    List<Invite> findAllBySettlementIdAndIsActiveTrue(Long settlementId);
}

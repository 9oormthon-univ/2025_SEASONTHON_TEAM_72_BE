package goorm.hackathon.pizza.repository;

import goorm.hackathon.pizza.entity.InviteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface InviteRepository extends JpaRepository<InviteEntity, Long> {

    Optional<InviteEntity> findByCodeAndIsActiveTrue(String code);

    Optional<InviteEntity> findFirstBySettlementIdAndIsActiveTrue(Long settlementId);

    List<InviteEntity> findAllBySettlementIdAndIsActiveTrue(Long settlementId);
}

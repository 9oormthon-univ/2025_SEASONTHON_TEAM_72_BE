package goorm.hackathon.pizza.repository;

import goorm.hackathon.pizza.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {

}

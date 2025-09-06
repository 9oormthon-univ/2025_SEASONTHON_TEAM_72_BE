package goorm.hackathon.pizza.repository;

import goorm.hackathon.pizza.entity.Participation;
import goorm.hackathon.pizza.entity.Settlement;
import goorm.hackathon.pizza.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    // 특정 유저가 특정 정산에 이미 참여했는지 확인하기 위한 메서드
    boolean existsBySettlementAndUser(Settlement settlement, User user);
}

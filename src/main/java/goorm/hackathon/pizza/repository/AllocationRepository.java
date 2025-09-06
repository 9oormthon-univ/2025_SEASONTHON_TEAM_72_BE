package goorm.hackathon.pizza.repository;

import goorm.hackathon.pizza.entity.Allocation;
import goorm.hackathon.pizza.entity.Item;
import goorm.hackathon.pizza.entity.Participation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AllocationRepository extends JpaRepository<Allocation, Long> {
    // 특정 참여자(Participation)와 특정 품목(Item)에 대한 분배 정보를 찾는 메서드
    Optional<Allocation> findByParticipationAndItem(Participation participation, Item item);
}

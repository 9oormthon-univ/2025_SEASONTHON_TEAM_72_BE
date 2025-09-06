package goorm.hackathon.pizza.repository;

import goorm.hackathon.pizza.dto.response.ItemInfoResponse;
import goorm.hackathon.pizza.entity.Enum.AllocationStatus;
import goorm.hackathon.pizza.entity.Item;
import goorm.hackathon.pizza.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    // 특정 정산에 포함된 모든 Item들을 조회
    List<Item> findAllBySettlement(Settlement settlement);

    // 특정 정산에 포함된 Item들의 상태별 개수를 세는 쿼리
    long countBySettlementAndStatus(Settlement settlement, AllocationStatus status);
}

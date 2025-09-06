package goorm.hackathon.pizza.repository;

import goorm.hackathon.pizza.dto.response.ItemInfoResponse;
import goorm.hackathon.pizza.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

}

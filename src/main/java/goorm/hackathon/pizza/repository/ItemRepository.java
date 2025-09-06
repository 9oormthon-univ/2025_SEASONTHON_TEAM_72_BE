package goorm.hackathon.pizza.repository;

import goorm.hackathon.pizza.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}

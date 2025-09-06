package goorm.hackathon.pizza.repository;

import goorm.hackathon.pizza.entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
}

package goorm.hackathon.pizza.repository;

import goorm.hackathon.pizza.entity.Invite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InviteRepository extends JpaRepository<Invite, Long> {

    Optional<Invite> findByCode(String code);
}

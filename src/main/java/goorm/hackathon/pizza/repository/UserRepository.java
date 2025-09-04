package goorm.hackathon.pizza.repository;

import goorm.hackathon.pizza.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByFirebaseUid(String firebaseUid);
    boolean existsByEmail(String email);
}

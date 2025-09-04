package goorm.hackathon.pizza.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter @Setter
@Entity
@Table(
        name = "users",
        indexes = {
                // firebase_uid 유니크 인덱스
                @Index(name = "idx_users_firebase_uid", columnList = "firebase_uid", unique = true),
                // email 유니크 인덱스
                @Index(name = "idx_users_email", columnList = "email", unique = true)
        }
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    // 이메일
    @Column(name = "email", length = 120, nullable = false, unique = true)
    private String email;

    // 닉네임
    @Column(name = "nickname", length = 50, nullable = false)
    private String nickname;

    // Firebase UID
    @Column(name = "firebase_uid", length = 128, nullable = false, unique = true)
    private String firebaseUid;

    // 가입 제공자
    @Column(name = "provider", length = 40, nullable = false)
    private String provider = "FIREBASE";

    // 권한 (Admin, User)
    @Column(name = "role", length = 20, nullable = false)
    private String role = "USER";

    // 생성 시각
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // 수정 시각
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

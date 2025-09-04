package goorm.hackathon.pizza.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "invites",
        uniqueConstraints = @UniqueConstraint(name = "uk_invites_code", columnNames = "code"),
        indexes = {
                @Index(name = "idx_invites_settlement", columnList = "settlementId"),
                @Index(name = "idx_invites_created_by", columnList = "createdBy")
        }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InviteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invite_id")
    private Long inviteId;

    @Column(name = "settlement_id", nullable = false)
    private Long settlementId;

    // 항상 대문자로 저장 (생성/저장 시 보정)
    @Column(name = "code", nullable = false, length = 64)
    private String code;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (isActive == null) isActive = true;
        if (code != null) code = code.toUpperCase(); // 안전망
    }
}

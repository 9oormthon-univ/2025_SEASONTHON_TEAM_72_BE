package goorm.hackathon.pizza.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "receipts")
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "receipt_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settlement_id", nullable = false)
    private Settlement settlement;

    @Column(nullable = false)
    private String imageUrl;

    @Lob // TEXT 타입 매핑
    private String ocrText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder
    public Receipt(Settlement settlement, String imageUrl, String ocrText, User createdBy) {
        this.settlement = settlement;
        this.imageUrl = imageUrl;
        this.ocrText = ocrText;
        this.createdBy = createdBy;
    }
}
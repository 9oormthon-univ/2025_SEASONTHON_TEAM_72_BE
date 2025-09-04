package goorm.hackathon.pizza.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "receipt_id")
    private Long id;

    // 어떤 정산에 대한 영수증인지 (Receipt 1 : 1 Settlement)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settlement_id", unique = true)
    private Settlement settlement;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    // 등록한 사용자 정보 (Receipt N : 1 User)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploader_id", nullable = false)
    private User uploader;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}

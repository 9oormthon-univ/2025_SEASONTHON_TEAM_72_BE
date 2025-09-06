package goorm.hackathon.pizza.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ItemInfoResponse {
    private Long itemId;
    private String name;
    private int price;
    private int count;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

package goorm.hackathon.pizza.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AllocationStatusCountResponse {
    private long completedCount;
    private long incompleteCount;
    private long totalCount;
}
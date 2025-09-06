package goorm.hackathon.pizza.dto.request;

import lombok.Data;

@Data
public class SettlementCreationRequest {
    private final String title;
    private Integer participantLimit;
}

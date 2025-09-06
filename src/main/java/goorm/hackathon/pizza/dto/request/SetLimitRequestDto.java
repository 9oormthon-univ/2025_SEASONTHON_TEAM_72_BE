package goorm.hackathon.pizza.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SetLimitRequestDto {
    private Integer participantLimit;
}
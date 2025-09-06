package goorm.hackathon.pizza.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SetLimitRequestDto {
    private Integer participantLimit;
}
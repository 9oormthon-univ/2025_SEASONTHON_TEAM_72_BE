package goorm.hackathon.pizza.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class CreateSettlementRequestDto {

    private String title;
    private List<ItemRequestDto> items;

}
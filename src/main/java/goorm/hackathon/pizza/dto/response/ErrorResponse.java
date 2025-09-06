package goorm.hackathon.pizza.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor // final 필드 생성자
public class ErrorResponse{
    private final int status;
    private final String message;
}


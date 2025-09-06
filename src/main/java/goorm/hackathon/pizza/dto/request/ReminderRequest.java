package goorm.hackathon.pizza.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReminderRequest {
    @JsonAlias({"user_id", "userId"})
    private Long userId; // 독촉받을 참여자 userId
}

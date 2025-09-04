package goorm.hackathon.pizza.dto.response;

public record InviteResponse(
        String code,
        String url,
        String expiresAt,
        boolean alreadyExists
) {}


package goorm.hackathon.pizza.util;

import goorm.hackathon.pizza.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class AuthUtils {
    private AuthUtils() {}

    public static Long currentUserIdOrThrow() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new IllegalStateException("Unauthenticated: SecurityContext is empty");
        }

        Object principal = auth.getPrincipal();

        // 1) 우리 User 엔티티로 들어온 경우
        if (principal instanceof User u) {
            if (u.getUserId() == null) {
                throw new IllegalStateException("Authenticated User has null userId");
            }
            return u.getUserId();
        }

        // 2) Long 또는 String (이전 가정)
        if (principal instanceof Long l) return l;
        if (principal instanceof String s) return Long.parseLong(s);

        // 3) 혹시 다른 구현체일 수도 있으니 name()도 시도 (id가 name에 있는 케이스)
        String name = auth.getName();
        try {
            return Long.parseLong(name);
        } catch (Exception ignore) {}

        throw new IllegalStateException("Unexpected principal type: " + principal.getClass());
    }
}

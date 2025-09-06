package goorm.hackathon.pizza.util;

import java.security.SecureRandom;

public final class InviteCodeGenerator {
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RND = new SecureRandom();

    private InviteCodeGenerator() {}

    public static String generate(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(ALPHABET.charAt(RND.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    // 사용자가 입력한 값 정규화(소문자 -> 대문자, 공백 제거)
    public static String normalize(String raw) {
        return raw == null ? null : raw.trim().toUpperCase();
    }
}

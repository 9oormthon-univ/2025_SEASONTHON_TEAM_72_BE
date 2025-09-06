//package goorm.hackathon.pizza.config;
//
//import goorm.hackathon.pizza.jwt.JwtTokenProvider;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Profile;
//import org.springframework.stereotype.Component;
//
//@Component
//@Profile("local") // 로컬에서만 실행되도록 (prod에서는 제외)
//@RequiredArgsConstructor
//public class JwtTestRunner implements CommandLineRunner {
//
//    private final JwtTokenProvider jwtTokenProvider;
//
//    @Override
//    public void run(String... args) {
//        // user_id = 1001, ROLE_USER 권한으로 JWT 생성
//        String token = jwtTokenProvider.createToken(1001L, "ROLE_USER");
//        System.out.println("===== TEST JWT TOKEN =====");
//        System.out.println("Bearer " + token);
//        System.out.println("==========================");
//    }
//}

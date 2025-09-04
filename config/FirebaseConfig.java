package goorm.hackathon.pizza.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Configuration
public class FirebaseConfig {

    // 지정된 서비스 계정 키 파일명
    @Value("${firebase.admin.service-account}")
    private String serviceAccountFile;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        // serviceAccountFile을 클래스패스에서 읽어와 Firebase 인증 설정
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(
                        new ClassPathResource(serviceAccountFile).getInputStream()
                ))
                .build();

        // FirebaseApp은 한 번만 초기화 가능 → 없으면 생성, 있으면 기존 인스턴스 반환
        if (FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.initializeApp(options);
        }
        return FirebaseApp.getInstance();
    }
}

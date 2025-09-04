package goorm.hackathon.pizza.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.admin.service-account}")
    private String serviceAccountFile;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        File file = new File(serviceAccountFile);

        FirebaseOptions options;

        if (file.exists()) {
            // 컨테이너(EC2)에서 /app/config/... 같은 실제 경로 파일 사용
            options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(new FileInputStream(file)))
                    .build();
            System.out.println("FirebaseConfig: Using file system resource -> " + file.getAbsolutePath());
        } else {
            // 로컬 개발 환경: resources/ 경로에서 읽음
            options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(
                            new ClassPathResource(serviceAccountFile).getInputStream()
                    ))
                    .build();
            System.out.println("FirebaseConfig: Using classpath resource -> " + serviceAccountFile);
        }

        if (FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.initializeApp(options);
        }
        return FirebaseApp.getInstance();
    }
}

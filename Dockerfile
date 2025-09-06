FROM eclipse-temurin:21-jre
WORKDIR /app

# 빌드된 fat jar 복사
ARG JAR_FILE=build/libs/pizza-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} /app/app.jar

# Firebase 서비스 계정 키 파일을 이미지 안으로 복사
COPY src/main/resources/firebase-service-account.json /app/config/firebase-service-account.json

# 애플리케이션이 참조할 경로를 환경변수로도 넣어둠(선택)
ENV FIREBASE_SERVICE_ACCOUNT_PATH=/app/config/firebase-service-account.json

EXPOSE 8080
ENTRYPOINT ["sh","-c","java -jar /app/app.jar --spring.profiles.active=prod"]

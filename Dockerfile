# 1. OpenJDK 21 기반 경량 이미지
FROM eclipse-temurin:21-jdk-alpine

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. Gradle Wrapper와 설정 파일 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# 4. 소스 코드 복사
COPY src src

# 5. 권한 부여 (Gradle Wrapper 실행 가능)
RUN chmod +x ./gradlew

# 6. 빌드
RUN ./gradlew bootJar -x test

# 7. 컨테이너 포트 설정
EXPOSE 8080

# 8. 앱 실행
CMD java -Dserver.port=$PORT -jar build/libs/board-0.0.1-SNAPSHOT.jar





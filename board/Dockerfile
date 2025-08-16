# 1. OpenJDK 21 기반 이미지 사용
FROM eclipse-temurin:21-jdk-alpine

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. Gradle Wrapper와 소스 코드 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

# 4. 권한 부여 (Gradle Wrapper 실행 가능하도록)
RUN chmod +x ./gradlew

# 5. 프로젝트 빌드
RUN ./gradlew build -x test

# 6. 실행 환경 설정
EXPOSE 8081

# 7. 앱 실행
CMD ["java", "-jar", "build/libs/board-0.0.1-SNAPSHOT.jar"]
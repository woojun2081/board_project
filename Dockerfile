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

# 5. 프로젝트 빌드 (bootJar 포함, 테스트 제외)
RUN ./gradlew bootJar -x test

# 6. Render가 지정하는 포트 환경변수 사용
EXPOSE ${PORT}

# 7. 앱 실행 (환경변수 PORT로 Spring Boot 포트 지정)
CMD ["sh", "-c", "java -jar build/libs/board-0.0.1-SNAPSHOT.jar --server.port=$PORT"]

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY . .
RUN ./gradlew clean build -x test
ENTRYPOINT ["sh", "-c", "java -jar build/libs/*.jar"]

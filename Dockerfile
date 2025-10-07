FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY . .
RUN ./gradlew clean build -x test
ENTRYPOINT ["sh", "-c", "java -jar build/libs/*.jar"]

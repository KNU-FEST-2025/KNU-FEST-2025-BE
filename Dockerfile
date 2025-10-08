FROM openjdk:17-jdk-slim
WORKDIR /app
COPY . .
RUN ./gradlew clean build -x test
RUN rm build/libs/*-plain.jar
ENTRYPOINT ["sh", "-c", "java -jar build/libs/*.jar"]

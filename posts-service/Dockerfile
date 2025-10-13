FROM openjdk:25-jdk-slim

WORKDIR /app

#COPY ./target/posts-service-0.0.1-SNAPSHOT.jar posts-service.jar
#EXPOSE 8080
#CMD ["java", "-jar", "posts-service.jar"]

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline

COPY src ./src

CMD ["./mvnw", "spring-boot:run"]
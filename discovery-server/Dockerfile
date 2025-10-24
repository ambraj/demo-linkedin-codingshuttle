ARG BUILDER_IMAGE=eclipse-temurin:25-jdk-alpine
ARG RUNTIME_IMAGE=eclipse-temurin:25-jre-alpine
ARG JAR_FILE=target/*.jar

FROM ${BUILDER_IMAGE} AS builder
WORKDIR /workspace

RUN apk add --no-cache maven

COPY pom.xml ./
RUN mvn -B -ntp dependency:go-offline

COPY src ./src
RUN mvn -B -ntp clean package -DskipTests

FROM ${RUNTIME_IMAGE} AS runtime
WORKDIR /app

ARG JAR_FILE
ENV JAVA_OPTS=""

COPY --from=builder /workspace/${JAR_FILE} app.jar

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
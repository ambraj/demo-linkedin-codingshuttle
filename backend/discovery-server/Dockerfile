ARG BUILDER_IMAGE=eclipse-temurin:25-jdk-alpine
ARG RUNTIME_IMAGE=eclipse-temurin:25-jre-alpine
ARG MAVEN_OPTS

FROM ${BUILDER_IMAGE} AS builder
WORKDIR /workspace

# Copy Maven wrapper first (changes rarely, cached layer)
COPY mvnw ./
COPY .mvn .mvn
RUN chmod +x mvnw

# Copy pom.xml for dependency resolution (cached if pom.xml unchanged)
COPY pom.xml ./
RUN ./mvnw -B -ntp dependency:go-offline ${MAVEN_OPTS}

# Copy source code last (changes frequently, rebuilds only this layer)
COPY src ./src
RUN ./mvnw -B -ntp clean package -DskipTests ${MAVEN_OPTS}

FROM ${RUNTIME_IMAGE} AS runtime
WORKDIR /app

ENV JAVA_OPTS=""

COPY --from=builder /workspace/target/*.jar app.jar

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
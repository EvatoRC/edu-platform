FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /build
RUN apk add --no-cache maven
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /build/target/course-service-1.0.0.jar app.jar
EXPOSE 8081
ENTRYPOINT ["sh", "-c", "\
  if [ -f /app/wallet/truststore.jks ]; then \
    keytool -importkeystore \
      -srckeystore /app/wallet/truststore.jks \
      -destkeystore $JAVA_HOME/lib/security/cacerts \
      -srcstorepass $WALLET_PASSWORD \
      -deststorepass changeit \
      -noprompt 2>/dev/null || true; \
  fi && \
  java -jar app.jar"]
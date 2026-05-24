FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /build
RUN apk add --no-cache maven
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /build/target/enrollment-service-1.0.0.jar app.jar
COPY Wallet_mi3rabdd/ /app/wallet/
RUN keytool -import -alias oracle-db \
    -keystore $JAVA_HOME/lib/security/cacerts \
    -storepass changeit \
    -noprompt \
    -file /app/wallet/ewallet.pem || true
EXPOSE 8083
ENTRYPOINT ["java", \
  "-Doracle.net.tns_admin=/app/wallet", \
  "-Djavax.net.ssl.trustStore=/app/wallet/truststore.jks", \
  "-Djavax.net.ssl.trustStoreType=JKS", \
  "-Djavax.net.ssl.trustStorePassword=OracleCloud_2025", \
  "-Djavax.net.ssl.keyStore=/app/wallet/keystore.jks", \
  "-Djavax.net.ssl.keyStoreType=JKS", \
  "-Djavax.net.ssl.keyStorePassword=OracleCloud_2025", \
  "-jar", "app.jar"]
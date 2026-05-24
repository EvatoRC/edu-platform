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
COPY Wallet_mi3rabdd/ /app/wallet/
RUN keytool -import -alias oracle-cloud-cert \
    -keystore $JAVA_HOME/lib/security/cacerts \
    -storepass changeit \
    -noprompt \
    -file /app/wallet/oracle-cert.pem
EXPOSE 8081
ENTRYPOINT ["java", \
  "-Doracle.net.tns_admin=/app/wallet", \
  "-jar", "app.jar"]
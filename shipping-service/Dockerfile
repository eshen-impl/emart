FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/shipping-service-0.0.1-SNAPSHOT.jar shipping-service.jar
ENTRYPOINT ["java", "-jar", "shipping-service.jar"]

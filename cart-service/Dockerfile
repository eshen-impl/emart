FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/cart-service-0.0.1-SNAPSHOT.jar cart-service.jar
ENTRYPOINT ["java", "-jar", "cart-service.jar"]

FROM eclipse-temurin:21-jammy
WORKDIR /opt/individualsapi
COPY build/libs/individualsapi-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
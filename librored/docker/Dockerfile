# Build Step
FROM maven:3.9-eclipse-temurin as build
WORKDIR /app
COPY backend/pom.xml .
COPY backend/src ./src
RUN mvn clean package -DskipTests=true

# Final Step - wait-for-it.sh
FROM eclipse-temurin:21-jre
WORKDIR /app

# Add wait-for-it
RUN apt-get update && apt-get install -y curl && \
    curl -o wait-for-it.sh https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh && \
    chmod +x wait-for-it.sh

COPY --from=build /app/target/LibroRed-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8443
CMD ["./wait-for-it.sh", "db:3306", "--", "java", "-jar", "app.jar", "--server.port=8443"]

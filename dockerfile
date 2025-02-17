FROM openjdk:21-slim 
WORKDIR /app
EXPOSE 8081
ADD target/Foyer-0.0.1-SNAPSHOT.jar foyer.jar
ENTRYPOINT ["java", "-jar", "foyer.jar"]

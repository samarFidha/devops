# Utiliser une image de base avec Java installé
FROM eclipse-temurin:17-jdk-jammy

# Définir le répertoire de travail dans le conteneur
WORKDIR /app



COPY target/Foyer-0.0.1-SNAPSHOT.jar app.jar

# Exposer le port sur lequel l'application Spring Boot écoute
EXPOSE 8080

# Commande pour exécuter l'application
ENTRYPOINT ["java", "-jar", "app.jar"]

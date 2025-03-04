# Étape 1 : Utiliser une image Alpine avec JDK
FROM openjdk:17-alpine

# Étape 2 : Définir le répertoire de travail
WORKDIR /app

# Étape 3 : Copier le fichier JAR dans l'image Docker
COPY target/Foyer-0.0.1-SNAPSHOT.jar app.jar

# Étape 4 : Exposer le port (exemple : 8080)
EXPOSE 8080

CMD ["java", "-jar", "app.jar"]


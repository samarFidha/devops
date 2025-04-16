# Utilisation d'une image Java pour exécuter l'application
FROM openjdk:11-jre-slim

# Spécifiez le répertoire de travail à l'intérieur du conteneur
WORKDIR /app

# Copiez le fichier JAR dans le conteneur
COPY ../target/Foyer-0.0.1-SNAPSHOT.jar /app/Foyer.jar

# Exposez le port sur lequel l'application écoute
EXPOSE 8080

# Commande à exécuter lorsque le conteneur démarre
ENTRYPOINT ["java", "-jar", "Foyer.jar"]

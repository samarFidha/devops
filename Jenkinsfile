pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'samarelfidha/alpine:1.0.0'  // Nom de votre image Docker
        DOCKER_CREDENTIALS_ID = 'samar-PAT'  // ID de vos identifiants Docker dans Jenkins
        GIT_CREDENTIALS_ID = 'PAT-SAMAR'  // ID des identifiants Git pour accéder à votre repo
       // SONAR_TOKEN = credentials('Jenkins-sonarqube-token')  // Token SonarQube
       // SONAR_HOST_URL = 'http://172.18.129.23:9000'  // URL de votre serveur SonarQube
    }

     stages {
            stage('Checkout Code') {
                steps {
                    git branch: 'Samar',
                        url: 'https://github.com/samarFidha/devops.git',
                        credentialsId: GIT_CREDENTIALS_ID  // Identifiants Git pour cloner le repo
                }
        }

        stage('Build with Maven') {
            steps {
                sh 'mvn clean package'
            }
        }



        stage('Build Docker Image') {
            steps {
                sh 'docker build -t $DOCKER_IMAGE .'
            }
        }

        stage('Push to Docker Hub') {
            steps {
                script {
                    // Authentification manuelle à Docker Hub
                    withCredentials([usernamePassword(credentialsId: DOCKER_CREDENTIALS_ID, passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
                        sh 'docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD'
                        sh 'docker push $DOCKER_IMAGE'
                    }
                }
            }
        }

        stage('Deploy Container') {
            steps {
                sh 'docker stop springboot-app || true'  // Arrêter le conteneur existant s'il existe
                sh 'docker rm springboot-app || true'    // Supprimer le conteneur existant s'il existe
                sh 'docker run -d -p 8081:8080 --name springboot-app $DOCKER_IMAGE'  // Lancer le nouveau conteneur
            }
        }
    }
}
//nexus
pipeline {
    agent any

    environment {
        //NEXUS_URL = 'http://172.18.129.23:8081'  // URL du serveur Nexus
        //NEXUS_REPOSITORY = 'maven-releases'  // Nom du dépôt dans Nexus
        ARTIFACT_NAME = 'Foyer-0.0.1-SNAPSHOT.jar'  // Nom de l'artefact généré par Maven
        GROUP_ID = 'tn/esprit/spring'  // Group ID Maven
        ARTIFACT_VERSION = '0.0.1-SNAPSHOT'  // Version de l'artefact
    }

    stages {
        stage('Checkout GitHub Repository') {
            steps {
                git branch: 'Samar',
                    url: 'https://github.com/samarFidha/devops.git',
                    credentialsId: 'CRE-SAFA'  // Utilisation du credential GitHub
            }
        }

        stage('Clean and Build Project') {
            steps {
                script {
                    echo 'Cleaning the project...'
                    sh 'mvn clean'  // Nettoyage du projet avec Maven

                    echo 'Building the project...'
                    sh 'mvn package -DskipTests'  // Compilation et packaging sans tests
                }
            }
        }


        }
    }
}
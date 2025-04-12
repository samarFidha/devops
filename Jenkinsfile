pipeline {
    agent any

    environment {
       // DOCKER_IMAGE = 'samarelfidha/alpine:1.0.0' // Nom de votre image Docker
        DOCKER_CREDENTIALS_ID = 'samar-PAT' // ID de vos identifiants Docker dans Jenkins
        GIT_CREDENTIALS_ID = 'PAT-SAMAR' // ID des identifiants Git pour accéder à votre repo
        ARTIFACT_NAME = 'Foyer-0.0.1-SNAPSHOT.jar' // Nom de l'artefact généré par Maven
        GROUP_ID = 'tn/esprit/spring' // Group ID Maven
        ARTIFACT_VERSION = '0.0.1-SNAPSHOT' // Version de l'artefact
    }

    stages {
        stage('Checkout Code') {
            steps {
                echo 'Checking out the code...'
                script {
                    git branch: 'Samar',
                        url: 'https://github.com/samarFidha/devops.git',
                        credentialsId: GIT_CREDENTIALS_ID
                }
            }
        }

        stage('Build with Maven') {
            steps {
                echo 'Building the Maven project...'
                script {
                    sh 'mvn clean package'
                    // Afficher la sortie de la construction
                    sh 'ls -l target/'
                    echo " target of artifact"
                    sh 'cat target/${ARTIFACT_NAME}.original' // Afficher le contenu du fichier artifact renommé

                }
            }
        }

        stage('Build Docker Image') {
            steps {
                echo 'Building Docker image...'
                script {
                        sh 'ls -l /workspace/devops/'
                       def imageName = "${env.JOB_NAME}:${env.BUILD_NUMBER}"

                       // Construire l'image Docker
                       sh "docker build -t ${imageName} -f /workspace/devops/docker/Dockerfile ."
                }
            }
        }

        stage('Push to Docker Hub') {
            steps {
                echo 'Pushing Docker image to Docker Hub...'
                script {
                    withCredentials([usernamePassword(credentialsId: DOCKER_CREDENTIALS_ID, passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
                        sh "docker login -u ${DOCKER_USERNAME} -p ${DOCKER_PASSWORD}"
                        sh "docker push ${DOCKER_IMAGE}"
                    }
                }
            }
        }

        stage('Deploy Container') {
            steps {
                echo 'Deploying the container...'
                sh 'docker stop springboot-app || true' // Stop existing container
                sh 'docker rm springboot-app || true' // Remove existing container
                sh "docker run -d -p 8081:8080 --name springboot-app ${DOCKER_IMAGE}" // Lancer le nouveau conteneur
            }
        }

        stage('Nexus') {
            steps {
                echo 'Configuring Nexus...'
                echo 'Cleaning the project...'
                sh 'mvn clean'

                echo 'Building the project...'
                sh 'mvn package -DskipTests'
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed. Please check the logs.'
        }
    }
}

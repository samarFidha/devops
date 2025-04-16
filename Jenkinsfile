pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'samarelfidha/alpine:latest'
        DOCKER_CREDENTIALS_ID = 'samar-PAT'
        GIT_CREDENTIALS_ID = 'PAT-SAMAR'
        ARTIFACT_NAME = 'Foyer-0.0.1-SNAPSHOT.jar'
        GROUP_ID = 'tn/esprit/spring'
        ARTIFACT_VERSION = '0.0.1-SNAPSHOT'
        SONAR_HOST_URL = 'http://172.30.46.120:9000/'
        SONAR_CREDENTIALS_ID = 'sonarqube-credentials'
    }

    stages {
        stage('Checkout Code') {
            steps {
                echo 'Checking out the code...'
                git branch: 'Samar',
                    url: 'https://github.com/samarFidha/devops.git',
                    credentialsId: GIT_CREDENTIALS_ID
            }
        }

 stage('MVN SONARQUBE') {
     steps {
         withSonarQubeEnv('SonarQube') {
             withCredentials([string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_TOKEN')]) {
                 // Affichage du token (juste pour debug)
                 sh 'echo "Token Sonar utilisé : $SONAR_TOKEN"'

                 // Lancement de l'analyse
                 sh 'mvn clean verify sonar:sonar -Dsonar.login=$SONAR_TOKEN'
             }
         }
     }
 }
        stage('Build with Maven') {
            steps {
                echo 'Building the Maven project...'
                sh 'mvn clean package'
                sh 'ls -l target/'
            }
        }

        stage('Build Docker Image') {
            steps {
                echo 'Building Docker image...'
                script {

                                       def imageName = "${env.JOB_NAME}:${env.BUILD_NUMBER}"

                                        sh ' cd docker'

                                        sh ' ls -l'

                                       // Construire l'image Docker

                                       sh 'docker build -t ${DOCKER_IMAGE}  .'

                                     //  sh 'docker build -t samar:1.0.0  -f /docker/Dockerfile .'

                }


            }
        }

        stage('Push to Docker Hub') {
            steps {
                echo 'Pushing Docker image to Docker Hub...'
                script {
                    withCredentials([usernamePassword(
                        credentialsId: DOCKER_CREDENTIALS_ID,
                        passwordVariable: 'DOCKER_PASSWORD',
                        usernameVariable: 'DOCKER_USERNAME'
                    )]) {
                        sh 'echo "${DOCKER_PASSWORD}" | docker login -u "${DOCKER_USERNAME}" --password-stdin'
                        sh "docker push ${DOCKER_IMAGE}"
                    }
                }
            }
        }

        stage('Deploy Container') {
            steps {
                echo 'Deploying the container...'
                sh 'docker stop springboot-app || true'
                sh 'docker rm springboot-app || true'
                sh '''
                    docker run -d -p 8081:8080 --name springboot-app ${DOCKER_IMAGE}
                '''
            }
        }

        stage('Nexus') {
            steps {
                echo 'Configuring Nexus...'
                sh 'mvn clean'
                sh 'mvn package -DskipTests'
            }
        }

        stage('Run Tests') {
            steps {
                echo 'Running tests...'
                sh 'mvn test'
            }
        }
    }

    post {
        success {
            echo '✅ Pipeline completed successfully!'
        }
        failure {
            echo '❌ Pipeline failed. Please check the logs.'
        }
    }
}

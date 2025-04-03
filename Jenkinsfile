pipeline {
    agent any
    environment {
        DOCKER_USER = credentials('dockerhub-credentials')
        SONARQUBE_CREDENTIALS = credentials('sonarqube-credentials')
        NEXUS_CREDENTIALS = credentials('nexus-credentials')
        NEXUS_REGISTRY_URL = '172.20.246.164:8082/repository'  // Without http://
        NEXUS_DOCKER_REPO = 'maven-releases'  // Your Docker repository name in Nexus
    }
    stages {
        stage('Checkout GitHub Repository') {
            steps {
                git branch: 'Chaima',
                    url: 'https://github.com/samarFidha/devops.git',
                    credentialsId: 'Jenkins'
            }
        }

        stage('Clean and Build Project') {
            steps {
                script {
                    echo 'Cleaning the project...'
                    sh 'mvn clean'

                    echo 'Building the project...'
                    sh 'mvn package -DskipTests'
                }
            }
        }

        stage('JUnit / Mockito Tests') {
            steps {
                sh 'mvn test'
            }
        }

        stage("Build Docker image") {
            steps {
                script {
                    sh "docker build -t foyer-app:latest ."
                }
            }
        }

        stage('SonarQube') {
            steps {
                sh "mvn sonar:sonar -Dsonar.login=$SONARQUBE_CREDENTIALS_USR -Dsonar.password=$SONARQUBE_CREDENTIALS_PSW"
            }
        }

        stage('Deploy to Nexus') {
            steps {
                script {
                    withCredentials([usernamePassword(
                        credentialsId: 'nexus-credentials',
                        usernameVariable: 'NEXUS_USER',
                        passwordVariable: 'NEXUS_PASS'
                    )]) {
                        sh """
                            echo "Authenticating to Nexus..."
                            docker login $NEXUS_REGISTRY_URL -u $NEXUS_USER -p $NEXUS_PASS

                            echo "Tagging image..."
                            docker tag foyer-app:latest $NEXUS_REGISTRY_URL/$NEXUS_DOCKER_REPO/foyer-app:latest

                            echo "Pushing image..."
                            docker push $NEXUS_REGISTRY_URL/$NEXUS_DOCKER_REPO/foyer-app:latest

                            echo "Logging out..."
                            docker logout
                        """
                    }
                }
            }
        }

        stage("Start app and db") {
            steps {
                sh "docker-compose up -d"
            }
        }
    }
}
pipeline {
    agent any
    environment {
        DOCKER_USER = credentials('dockerhub-credentials')
        SONARQUBE_CREDENTIALS = credentials('sonarqube-credentials')
        NEXUS_CREDENTIALS = credentials('nexus-credentials')
        NEXUS_BASE_URL = '172.20.246.164:8082/repository'  // Without http://
        NEXUS_REPOSITORY = 'maven-releases'  // Your Docker repository name in Nexus
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
                        sh "docker build \
                                                          --build-arg NEXUS_BASE_URL=${NEXUS_BASE_URL} \
                                                          --build-arg NEXUS_REPOSITORY=${NEXUS_REPOSITORY} \

                                                          -t foyer-app:latest ."
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
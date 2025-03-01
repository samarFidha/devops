pipeline {
    agent any

    triggers {
        pollSCM('H/5 * * * *') // Checks for changes every 5 minutes
    }

    environment {
        DOCKER_IMAGE = "amenallahbelhouichet/dev:latest"
        DOCKER_CREDENTIALS_ID = "docker-hub-credentials"
    }

    stages {
        stage('Clone Repository') {
            steps {
                git branch: 'main', url: 'https://github.com/samarFidha/dev.git'
            }
        }

        stage('Setup Maven') {
            steps {
                echo 'Setting up Maven...'
            }
        }

        stage('Maven Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    sh "docker build -t $DOCKER_IMAGE ."
                }
            }
        }

        stage('Push Image to Docker Hub') {
            steps {
                script {
                    withDockerRegistry([credentialsId: DOCKER_CREDENTIALS_ID, url: '']) {
                        sh "docker push $DOCKER_IMAGE"
                    }
                }
            }
        }
    }

    post {
        success {
            echo 'Build and Deployment Successful!'
        }
        failure {
            echo 'Build Failed!'
        }
    }
}


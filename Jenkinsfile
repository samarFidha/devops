pipeline {
    agent any
    environment {
        DOCKER_IMAGE = 'moatezg/nginx:1.0.0'
    }
    stages {
        stage('Checkout') {
            steps {
                git credentialsId: 'git-credentials	', branch: 'moatez',
                    url: 'https://github.com/samarFidha/devops.git'
            }
        }

        stage('Clean Workspace') {
            steps {
                sh 'mvn clean'
            }
        }

        stage('Build Project') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    sh "docker build -t $DOCKER_IMAGE ."
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'docker-hub-credentials', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                        sh "docker login -u $DOCKER_USER -p $DOCKER_PASS"
                        sh "docker push $DOCKER_IMAGE"
                    }
                }
            }
        }

        stage('Pull Docker Image') {
            steps {
                script {
                    sh "docker pull $DOCKER_IMAGE"
                }
            }
        }
    }
}

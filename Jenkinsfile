pipeline {
    agent any
    environment {
        DOCKER_IMAGE = 'bechirgarali/foyer-app:latest'
    }
    stages {
        stage('Checkout Code') {
            steps {
                checkout([$class: 'GitSCM',
                    branches: [[name: 'bechir']],
                    userRemoteConfigs: [[
                        url: 'https://github.com/samarFidha/devops.git',
                        credentialsId: 'token'
                    ]]
                ])
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
                    withCredentials([usernamePassword(credentialsId: 'docker', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                        sh "echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin"
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

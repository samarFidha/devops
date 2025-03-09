pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "amenallahbelhouichet/dev:latest"
    }

    stages {
        stage('Clone Repository') {
            steps {
                script {
                    checkout([
                        $class: 'GitSCM',
                        branches: [[name: 'amen']],  
                        userRemoteConfigs: [[
                            url: 'https://github.com/samarFidha/devops.git',
                            credentialsId: 'jenkins'  
                        ]]
                    ])
                }
            }
        }

        stage('Setup Maven') {
            steps {
                sh 'echo "Setting up Maven..."'
                sh 'mvn --version'
            }
        }

        stage('Maven Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Run Tests with Spring Profile') {  // ✅ Moved inside 'stages'
            steps {
                sh 'mvn test -Dspring.profiles.active=test'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t ${DOCKER_IMAGE} .'
            }
        }

        stage('Push to Docker Hub') {
            steps {
                withDockerRegistry([credentialsId: 'docker-hub-credentials', url: 'https://github.com/samarFidha/devops.git']) {
                    sh 'docker push ${DOCKER_IMAGE}'
                }
            }
        }
    }
}

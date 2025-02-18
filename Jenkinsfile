pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "moatezg/nginx:1.0.0"
        DOCKER_REGISTRY_URL = 'https://registry.hub.docker.com'
    }

    stages {
        stage('Clone Repository') {
            steps {
                checkout scm
            }
        }

        stage('Maven Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Run Unit Tests') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t ${DOCKER_IMAGE} .'
            }
        }

        stage('Push to Docker Hub') {
            steps {
                script {
                    withDockerRegistry([credentialsId: 'docker-hub-credentials', url: DOCKER_REGISTRY_URL]) {
                        sh 'docker push ${DOCKER_IMAGE}'
                    }
                }
            }
        }

        stage('Deploy Container') {
            steps {
                script {
                    // Stop and remove existing container if it exists
                    sh '''
                    docker ps -aq -f name=my-nginx-container | grep -q . && \
                    docker stop my-nginx-container && \
                    docker rm my-nginx-container
                    '''

                    // Run the new container
                    sh 'docker run -d --restart=always -p 8081:80 --name my-nginx-container ${DOCKER_IMAGE}'
                }
            }
        }
    }
}

pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "moatezg/my-nginx:latest"
    }

    stages {
        stage('Clone Repository') {
            steps {
                script {
                    checkout([
                        $class: 'GitSCM',
                        branches: [[name: 'moatez']],
                        userRemoteConfigs: [[
                            url: 'https://github.com/samarFidha/devops.git',
                            credentialsId: 'git-credentials'
                        ]]
                    ])
                }
            }
        }

        stage('Setup Maven') {
            steps {
                sh 'echo "Setting up Maven..."'
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
                withDockerRegistry([credentialsId: 'docker-hub-credentials', url: '']) {
                    sh 'docker push ${DOCKER_IMAGE}'
                }
            }
        }

        stage('Deploy Container') {
            steps {
                script {
                    // Stop and remove the existing container safely
                    sh """
                    if docker ps -aq -f name=my-nginx-container | grep -q .; then
                        docker stop my-nginx-container || true
                        docker rm my-nginx-container || true
                    fi
                    """

                    // Run the new container with auto-restart
                    sh 'docker run -d --restart=always -p 8081:80 --name my-nginx-container ${DOCKER_IMAGE}'
                }
            }
        }
    }
}

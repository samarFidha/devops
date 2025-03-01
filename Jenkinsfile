pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "amenallahbelhouichet/dev"   // Updated image name
        DOCKER_TAG = "latest"
    }

    stages {
        stage('Clone Repository') {
            steps {
                script {
                    checkout([
                        $class: 'GitSCM',
                        branches: [[name: 'amen']],  // Ensure correct branch
                        userRemoteConfigs: [[
                            url: 'https://github.com/samarFidha/devops.git',
                            credentialsId: 'jenkins'  // Ensure Jenkins has access
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
                sh '''
                    docker build -t $DOCKER_IMAGE:$DOCKER_TAG .
                '''
            }
        }

        stage('Push Image to Docker Hub') {
            steps {
                withCredentials([string(credentialsId: 'docker-hub-token', variable: 'DOCKER_HUB_PASS')]) {
                    sh '''
                        echo "$DOCKER_HUB_PASS" | docker login -u "amenallahbelhouichet" --password-stdin
                        docker push $DOCKER_IMAGE:$DOCKER_TAG
                    '''
                }
            }
        }

        stage('Pull Image from Docker Hub') {
            steps {
                sh '''
                    docker pull $DOCKER_IMAGE:$DOCKER_TAG
                '''
            }
        }
    }
}

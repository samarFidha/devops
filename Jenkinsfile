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
                git branch: 'main',
                    url: 'https://github.com/samarFidha/devops.git',
                    credentialsId: GIT_CREDENTIALS_ID
            }
        }

       stage('SonarQube Analysis') {
           steps {
               withCredentials([string(credentialsId: SONAR_CREDENTIALS_ID, variable: 'SONAR_TOKEN')]) {
                   withSonarQubeEnv('SonarQube') {
                       sh '''
                           sonar-scanner \
                           -Dsonar.projectKey=devops \
                           -Dsonar.sources=src \
                           -Dsonar.login=$SONAR_TOKEN
                       '''

                   }
               }
           }
       }


        stage('Quality Gate') {
            steps {
                timeout(time: 1, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
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
                dir('docker') {
                    sh 'ls -l'
                    sh "docker build -t ${DOCKER_IMAGE} ."
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
                sh "docker run -d -p 8081:8080 --name springboot-app ${DOCKER_IMAGE}"
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
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed. Please check the logs.'
        }
    }
}

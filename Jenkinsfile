pipeline {
    agent any
    environment {
        DOCKER_USER = credentials('dockerhub-credentials')
        SONARQUBE_CREDENTIALS = credentials('sonarqube-credentials')
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

        stage('Dockerhub') {
            steps {
                sh "docker login -u $DOCKER_USER_USR -p $DOCKER_USER_PSW"
                sh "docker tag foyer-app:latest $DOCKER_USER_USR/foyer-app:latest"
                sh "docker push $DOCKER_USER_USR/foyer-app:latest"
            }
        }

        stage('SonarQube') {
            steps {
                sh "mvn sonar:sonar -Dsonar.login=$SONARQUBE_CREDENTIALS_USR -Dsonar.password=$SONARQUBE_CREDENTIALS_PSW"
            }
        }

        stage("Start app and db") {
            steps {
                sh "docker-compose up -d"
            }
        }
    }
}

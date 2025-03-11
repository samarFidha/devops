pipeline {
    agent any
    environment {
        DOCKER_IMAGE = 'moatezg/nginx:1.0.0'
        SONARQUBE_CREDENTIALS = credentials('sonarqube-credentials')
    }
    stages {
        stage('Checkout') {
            steps {
                git(
                    credentialsId: 'github_docker',
                    branch: 'moatez',
                    url: 'git@github.com:samarFidha/devops.git'
                )
            }
        }

        stage('Clean Workspace') {
            steps {
                sh 'mvn clean'
            }
        }

        stage('Build Project') {
            steps {
                sh 'mvn package' // Removed -DskipTests for better quality control
            }
        }

        stage('JUnit / Mockito Tests') {
                    steps {
                        sh 'mvn test'
                    }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    sh 'test -f Dockerfile && echo "Dockerfile found" || exit 1'
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
                        sh 'docker logout' // Log out after pushing
                    }
                }
            }
        }

        stage('Pull Docker Image') {
            steps {
                script {
                    sh "docker pull $DOCKER_IMAGE"
                    sh "docker inspect $DOCKER_IMAGE" // Verify the image was pulled
                }
            }
        }

        stage('SonarQube') {
            steps {
                withCredentials([string(credentialsId: 'sonarqube-credentials', variable: 'SONAR_TOKEN')]) {
                    sh "mvn sonar:sonar -Dsonar.login=$SONAR_TOKEN"
                }
            }
        }

        stage('Deploy to Nexus') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'nexus-deploy-credentials', usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASS')]) {
                    sh """
                         mvn deploy -s /home/moatez/.m2/settings.xml \
                            -DrepositoryId=deploymentRepo \
                            -Durl=http://172.19.181.217:8081/repository/maven-releases/ \
                            -Dusername=$NEXUS_USER \
                            -Dpassword=$NEXUS_PASS
                    """
                }
            }
        }
    }
}
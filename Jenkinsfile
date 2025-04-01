pipeline {
    agent any
    environment {
        DOCKER_IMAGE = 'bechirgarali/foyer-app:latest'
        NEXUS_URL = 'http://172.24.32.66:8081'
        MAVEN_SETTINGS_PATH = '/usr/share/maven/conf/settings.xml'
    }
    stages {
        stage('Checkout Code') {
            steps {
                checkout([$class: 'GitSCM',
                    branches: [[name: 'bechir']],
                    userRemoteConfigs: [[
                        url: 'https://github.com/samarFidha/devops.git',
                        credentialsId: 'token'
                    ]]]
                )
            }
        }
        stage('Clean Workspace') {
            steps {
                sh 'mvn clean'
            }
        }

        stage('Run Tests with Spring Profile') {
            steps {
                sh 'mvn test -Dspring.profiles.active=test'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                script {
                    withSonarQubeEnv('SonarQube') {
                        withCredentials([string(credentialsId: 'sonarToken', variable: 'SONAR_TOKEN')]) {
                            sh '''
                                mvn verify sonar:sonar \
                                  -Dsonar.projectKey=sonar \
                                  -Dsonar.projectName='sonar' \
                                  -Dsonar.host.url=http://localhost:9000 \
                                  -Dsonar.login=$SONAR_TOKEN \
                                  -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                            '''
                        }
                    }
                }
            }
        }

        stage('Deploy to Nexus') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'nexus', usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASS')]) {
                        sh '''
                            mvn deploy \
                              -s ${MAVEN_SETTINGS_PATH} \
                              -DaltDeploymentRepository=maven-releases::default::${NEXUS_URL}/repository/maven-releases/
                        '''
                    }
                }
            }
        }


        stage('Build Docker Image') {
            steps {
                sh "docker build -t $DOCKER_IMAGE ."
            }
        }

        stage('Push Docker Image') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'docker', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                        sh "echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin"
                        sh "docker push $DOCKER_IMAGE"
                        sh "docker logout"
                    }
                }
            }
        }

        stage('Pull Docker Image') {
            steps {
                sh "docker pull $DOCKER_IMAGE"
            }
        }
    }
    post {
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed! Please check the logs.'
        }
    }
}

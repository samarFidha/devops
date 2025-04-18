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

        stage('Run Tests with Spring Profile') {
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
                withDockerRegistry([credentialsId: 'docker-hub-token', url: '']) {
                    sh "docker push ${DOCKER_IMAGE}"
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                script {
                    withSonarQubeEnv('SonarQube') {
                        withCredentials([string(credentialsId: 'sonarToken', variable: 'SONAR_TOKEN')]) {
                            sh '''
                                mvn clean install
                                mvn clean verify sonar:sonar \
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

        stage('Deploy with Docker Compose') {
            steps {
                script {
                    sh '''
                        echo "Stopping existing containers..."
                        docker compose down --remove-orphans || true

                        if docker compose version >/dev/null 2>&1; then
                            echo "Using modern Docker Compose (docker compose)"
                            docker compose up -d
                        elif command -v docker-compose >/dev/null 2>&1; then
                            echo "Using legacy Docker Compose (docker-compose)"
                            docker-compose up -d
                            echo "Waiting for containers to initialize..."
                            sleep 30
                        else
                            echo "ERROR: No Docker Compose command available"
                            exit 1
                        fi

                        echo "Checking container health statuses..."
                        UNHEALTHY=$(docker ps --filter 'health=unhealthy' --format '{{.Names}}')
                        if [ -z "$UNHEALTHY" ]; then
                            echo "✅ All containers are healthy."
                        else
                            echo "❌ Some containers are unhealthy: $UNHEALTHY"
                            docker ps -a
                            echo "Logs from foyer-db:"
                            docker logs foyer-db || true
                            echo "Logs from foyer-app:"
                            docker logs foyer-app || true
                            exit 1
                        fi
                    '''
                }
            }
        }

        stage('Deploy to Nexus') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'nexus-deploy-credentials', usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASS')]) {
                    sh """
                        mvn deploy -s /usr/share/maven/conf/settings.xml \
                            -DrepositoryId=nexus-snapshots \
                            -Durl=http://172.30.201.44:8081/repository/maven-snapshots/ \
                            -Dusername=$NEXUS_USER \
                            -Dpassword=$NEXUS_PASS
                    """
                }
            }
        }
    }

    post {
        always {
            script {
                if (currentBuild.result == 'FAILURE') {
                    sh '''
                        echo "🔍 Docker container status:"
                        docker ps -a
                        echo "📄 Logs from foyer-db:"
                        docker logs foyer-db || true
                        echo "📄 Logs from foyer-app:"
                        docker logs foyer-app || true
                    '''
                }
            }
            cleanWs()
        }
    }
}

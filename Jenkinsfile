pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'bechirgarali/foyer-app:latest'
        NEXUS_URL = 'http://172.24.32.66:8081'
        NEXUS_REPO_RELEASES = 'maven-releases'
        NEXUS_REPO_SNAPSHOTS = 'maven-snapshots'
    }

    stages {
        stage('Force Clean Workspace') {
            steps {
                script {
                    echo '🧹 Forcing manual workspace cleanup...'
                    deleteDir() // supprime tous les fichiers du workspace
                }
            }
        }

        stage('Clean GIT') {
            steps {
                cleanWs()
            }
        }

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

        stage('Run Tests') {
            steps {
                sh 'mvn test -Dspring.profiles.active=test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/**/*.xml'
                    archiveArtifacts '**/target/surefire-reports/**/*.*'
                }
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
                   withCredentials([usernamePassword(credentialsId: 'nexusCredentials', usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASS')]) {
                       // Get the project version
                       def projectVersion = sh(script: "mvn help:evaluate -Dexpression=project.version -q -DforceStdout", returnStdout: true).trim()

                       // Define Nexus repository based on version
                       def repo = projectVersion.endsWith('-SNAPSHOT') ? NEXUS_REPO_SNAPSHOTS : NEXUS_REPO_RELEASES

                       // Deploy to Nexus
                       sh """
                           echo "🔁 Deploying to Nexus Repository: ${repo}"
                           mvn clean deploy \
                           -DskipTests \
                           -DaltDeploymentRepository=${repo}::default::${NEXUS_URL}/repository/${repo} \
                           -Dnexus.username=${NEXUS_USER} \
                           -Dnexus.password=${NEXUS_PASS}
                       """
                   }
               }
           }
       }



        stage('Build Application') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    sh "docker build -t ${DOCKER_IMAGE} ."
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'docker', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                        sh "echo ${DOCKER_PASS} | docker login -u ${DOCKER_USER} --password-stdin"
                        sh "docker push ${DOCKER_IMAGE}"
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

        success {
            echo '✅ Pipeline completed successfully!'
        }

        failure {
            echo '❌ Pipeline failed! Please check the logs.'
        }
    }
}

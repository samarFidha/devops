pipeline {
    agent any
    environment {
        DOCKER_IMAGE = 'bechirgarali/foyer-app:latest'
        NEXUS_URL = 'http://172.24.32.66:8081'
        NEXUS_REPO_RELEASES = 'maven-releases'
        NEXUS_REPO_SNAPSHOTS = 'maven-snapshots'
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

        stage('Build Application') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }

        stage('Deploy to Nexus') {
             steps {
                 script {
                     withCredentials([usernamePassword(credentialsId: 'nexusCredentials', usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASS')]) {
                         // Get the project version
                         def projectVersion = sh(script: "mvn help:evaluate -Dexpression=project.version -q -DforceStdout", returnStdout: true).trim()

                         // Define Nexus repository based on version
                         def repo = projectVersion.endsWith('-SNAPSHOT') ? 'maven-snapshots' : 'maven-releases'

                         // Deploy to Nexus
                         sh """
                             echo "Deploying to Nexus Repository: ${repo}"
                             mvn clean deploy \
                             -s /usr/share/maven/conf/settings.xml \
                             -DskipTests
                         """
                     }
                 }
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
                       # Stop and remove any existing containers
                       docker-compose down || true

                       # Start services with clean build
                       docker-compose up -d --build

                       # Wait for database to become healthy (without password)
                       echo "Waiting for database to initialize..."
                       timeout 180s bash -c 'until docker-compose exec -T data mysqladmin ping -uroot --silent; do sleep 5; done'

                       # Verify services
                       docker-compose ps
                   '''
               }
           }
       }

    }
    post {
           always {
               // Capture docker logs if pipeline fails
               script {
                   if (currentBuild.result == 'FAILURE') {
                       sh '''
                           echo "Docker container status:"
                           docker ps -a
                           echo "Logs from foyer-db:"
                           docker logs foyer-db || true
                           echo "Logs from foyer-app:"
                           docker logs foyer-app || true
                       '''
                   }
               }
               cleanWs()
           }
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed! Please check the logs.'
        }

    }
}
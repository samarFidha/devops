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
            // Retrieve Nexus credentials
            withCredentials([usernamePassword(credentialsId: 'nexusCredentials', usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASS')]) {

                // Get the project version from Maven
                def projectVersion = sh(script: "mvn help:evaluate -Dexpression=project.version -q -DforceStdout", returnStdout: true).trim()

                // Determine the repository based on project version (SNAPSHOT or RELEASE)
                def repo = projectVersion.endsWith('-SNAPSHOT') ? NEXUS_REPO_SNAPSHOTS : NEXUS_REPO_RELEASES

                // Debugging output for credentials and deployment info
                echo "Deploying version: ${projectVersion} to repository: ${repo}"

                // Deploy to Nexus using the credentials
                sh """
                    mvn clean deploy -X \
                    -DaltDeploymentRepository=${repo}::default::${NEXUS_URL}/repository/${repo}/ \
                    -Dusername=${NEXUS_USER} \
                    -Dpassword=${NEXUS_PASS} \
                    -s /var/jenkins_home/.m2/settings.xml
                """
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

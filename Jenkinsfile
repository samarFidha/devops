pipeline {
    agent any

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
    }
}
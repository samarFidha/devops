pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'samarelfidha/alpine:latest'
        ARTIFACT_NAME = 'Foyer-0.0.1-SNAPSHOT.jar'
        GROUP_ID = 'tn/esprit/spring'
        ARTIFACT_VERSION = '0.0.1-SNAPSHOT'
        SONAR_HOST_URL = 'http://172.30.46.120:9000/'
        DOCKER_CREDENTIALS_ID = 'samar-PAT'
        GIT_CREDENTIALS_ID = 'PAT-SAMAR'
        NEXUS_CREDENTIALS_ID= 'nexus-hub-credentials'
        SONAR_CREDENTIALS_ID = 'sonarqube-credentials'

    }

    stages {
        stage('Checkout Code') {
            steps {
                echo 'Checking out the code...'
                git branch: 'Samar',
                    url: 'https://github.com/samarFidha/devops.git',
                    credentialsId: GIT_CREDENTIALS_ID
            }
        }






 stage('MVN SONARQUBE') {
     steps {
         withSonarQubeEnv('SonarQube') {
             withCredentials([string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_TOKEN')]) {

                 sh 'echo "Token Sonar utilisé : $SONAR_TOKEN"'


                 sh 'mvn clean verify sonar:sonar -Dsonar.login=$SONAR_TOKEN'
             }
         }
     }
 }





       stage('Build with Maven') {

                   steps {

                       echo 'Building the Maven project...'

                       script {

                           sh 'mvn clean package'

                           sh 'ls -l target/'

                           echo " target of artifact"

                       }

                   }
                   }





        stage('Build Docker Image') {
            steps {
                echo 'Building Docker image...'
                script {

                                       def imageName = "${env.JOB_NAME}:${env.BUILD_NUMBER}"

                                    sh 'ls -l' // juste pour vérifier les fichiers
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
         sh '''
             # Arrêter et supprimer l'ancien conteneur nommé springboot-app
             docker stop springboot-app || true
             docker rm springboot-app || true

             # Arrêter et supprimer tout conteneur qui utilise déjà le port 8081
             docker ps --filter "publish=8081" --format "{{.ID}}" | xargs -r docker stop
             docker ps -a --filter "publish=8081" --format "{{.ID}}" | xargs -r docker rm

             # Lancer le nouveau conteneur
             docker run -d -p 8081:8080 --name springboot-app ${DOCKER_IMAGE}
         '''
     }
 }


    stage('Deploy to Nexus') {
               steps {
                   withCredentials([usernamePassword(credentialsId: 'nexus-deploy-credentials', usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASS')]) {
                       sh """
                           mvn deploy -s /usr/share/maven/conf/settings.xml \
                               -DrepositoryId=nexus-snapshots \
                               -Durl=http://172.30.46.120:8081/repository/maven-snapshots/ \
                               -Dusername=$NEXUS_USER \
                               -Dpassword=$NEXUS_PASS
                       """
                   }
               }
           }

}


    post {
        success {
            echo '✅ Pipeline completed successfully!'
        }
        failure {
            echo '❌ Pipeline failed. Please check the logs.'
        }
    }
}

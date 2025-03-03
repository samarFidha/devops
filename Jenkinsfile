
pipeline {
    agent any
    environment {
        DOCKER_HUB_USER = 'chaimanaouali'
        DOCKER_HUB_PASSWORD = credentials('dockerhub-credentials') // Use Jenkins credentials ID
    }
    stages {
        stage('Checkout GitHub Repository') {
            steps {
                // Checkout code from GitHub repository using the correct credentials
                git branch: 'Chaima',
                    url: 'https://github.com/samarFidha/devops.git',
                    credentialsId: 'Jenkins'  // The ID of the credentials you added
            }
        }

        stage('Clean and Build Project') {
            steps {
                script {
                    // Nettoyer le projet (par exemple avec Maven ou Gradle)
                    echo 'Cleaning the project...'
                    sh 'mvn clean' // Si vous utilisez Maven pour nettoyer le projet

                    // Créer le livrable sous le dossier target (éliminer la phase de test)
                    echo 'Building the project...'
                    sh 'mvn package -DskipTests' // Maven pour créer le livrable dans le dossier target en sautant les tests
                }
            }
        }
 stage('JUnit / Mockito Tests') {
                            steps {
                                // Run JUnit and Mockito tests using Maven
                                sh 'mvn test'
                            }
                        }
         stage("Build Docker image") {
                      steps {
                          script {
                              // Build Docker image using the JAR file from Nexus
                              sh " docker build -t foyer-app:latest ."
                          }
                      }
                  }


         stage('dockerhub') {
                                          steps {

                                     sh 'echo $DOCKER_HUB_PASSWORD | docker login -u $DOCKER_HUB_USER --password-stdin'
                                     sh " docker tag foyer-app:latest chaimanaouali/foyer-app:latest"
                                     sh " docker push  chaimanaouali/foyer-app:latest"
                                          }
                    }

                    stage('SonarQube') {
                               steps {
                                   withCredentials([string(credentialsId: 'sonar-password', variable: 'SONAR_PASSWORD')]) {
                                       sh 'mvn sonar:sonar -Dsonar.login=admin -Dsonar.password=$SONAR_PASSWORD'
                                   }
                               }
                           }

 stage("Start app and db") {
            steps {
                sh "docker-compose up -d"
            }
        }

    }
}
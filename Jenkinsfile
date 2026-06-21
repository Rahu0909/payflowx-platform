pipeline {

    agent any

    tools {
        maven 'maven3'
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main',
                    credentialsId: 'github-creds',
                    url: 'https://github.com/Rahu0909/payflowx-platform.git'
            }
        }

        stage('Build Services') {

            parallel {

                stage('Discovery Server') {
                    steps {
                        dir('services/discovery-server') {
                            sh 'chmod +x mvnw'
                            sh './mvnw clean package -DskipTests'
                        }
                    }
                }

                stage('API Gateway') {
                    steps {
                        dir('services/api-gateway') {
                            sh 'chmod +x mvnw'
                            sh './mvnw clean package -DskipTests'
                        }
                    }
                }

                stage('Auth Service') {
                    steps {
                        dir('services/auth-service') {
                            sh 'chmod +x mvnw'
                            sh './mvnw clean package -DskipTests'
                        }
                    }
                }

                stage('User Service') {
                    steps {
                        dir('services/user-service') {
                            sh 'chmod +x mvnw'
                            sh './mvnw clean package -DskipTests'
                        }
                    }
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                dir('services/discovery-server') {
                    withSonarQubeEnv('sonarqube') {
                        sh '''
                        ./mvnw sonar:sonar \
                        -Dsonar.projectKey=payflowx-discovery-server \
                        -Dsonar.projectName=payflowx-discovery-server
                        '''
                    }
                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully'
        }

        failure {
            echo 'Pipeline failed'
        }
    }
}
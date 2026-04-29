pipeline {
    agent any

    tools {
        maven 'Maven'
        jdk 'JDK21'
    }

    environment {
        SONAR_PROJECT_KEY = 'rawi'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test -Dspring.profiles.active=ci'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('Rawi-SonarQube') {
                    sh 'mvn sonar:sonar -Dsonar.projectKey=${SONAR_PROJECT_KEY}'
                }
            }
        }
    }

    post {
        success {
            sh '''
                curl -s -X POST "https://api.telegram.org/bot${TELEGRAM_BOT_TOKEN}/sendMessage" \
                    -d chat_id="${TELEGRAM_CHAT_ID}" \
                    -d text="✅ Rawi build #${BUILD_NUMBER} passed"
            '''
        }
        failure {
            sh '''
                curl -s -X POST "https://api.telegram.org/bot${TELEGRAM_BOT_TOKEN}/sendMessage" \
                    -d chat_id="${TELEGRAM_CHAT_ID}" \
                    -d text="❌ Rawi build #${BUILD_NUMBER} failed — ${BUILD_URL}"
            '''
        }
    }
}

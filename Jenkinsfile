pipeline {
    agent any

    options {
        timestamps()
    }

    environment {
        MAVEN_OPTS = '-Xmx1024m'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Start environment') {
            steps {
                sh 'docker compose -f doker-compose.yml up -d'
            }
        }

        stage('Run tests') {
            steps {
                sh 'mvn clean test'
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
            sh 'docker compose -f doker-compose.yml down || true'
        }
    }
}
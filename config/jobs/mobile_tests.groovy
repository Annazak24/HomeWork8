pipeline {
    agent any

    tools {
        allure 'Allure 2.30'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Run Mobile Tests') {
            steps {
                sh 'mvn clean test -Dmaven.test.failure.ignore=true'
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: '**/allure-results/**', allowEmptyArchive: true

            allure([
                    includeProperties: false,
                    jdk: '',
                    properties: [],
                    reportBuildPolicy: 'ALWAYS',
                    results: [[path: 'target/allure-results']]
            ])
        }
    }
}
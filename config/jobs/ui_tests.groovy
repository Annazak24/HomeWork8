pipeline {
    agent any

    tools {
        allure 'Allure 2.30'
    }

    parameters {
        text(
                name: 'CONFIG',
                defaultValue: '''browser: chrome
base_url: https://example.com
remote: false''',
                description: 'UI test config'
        )
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Prepare config') {
            steps {
                writeFile file: 'config.yaml', text: params.CONFIG

                script {
                    env.BROWSER = sh(
                            script: "grep '^browser:' config.yaml | cut -d':' -f2- | xargs",
                            returnStdout: true
                    ).trim()

                    env.BASE_URL = sh(
                            script: "grep '^base_url:' config.yaml | cut -d':' -f2- | xargs",
                            returnStdout: true
                    ).trim()

                    env.REMOTE = sh(
                            script: "grep '^remote:' config.yaml | cut -d':' -f2- | xargs",
                            returnStdout: true
                    ).trim()

                    echo "Browser: ${env.BROWSER}"
                    echo "Base URL: ${env.BASE_URL}"
                    echo "Remote: ${env.REMOTE}"
                }
            }
        }

        stage('Run UI Tests') {
            steps {
                sh """
                    mvn test \
                      -Dbrowser=${env.BROWSER} \
                      -Dbase.url=${env.BASE_URL} \
                      -Dremote=${env.REMOTE} \
                      -Dmaven.test.failure.ignore=true
                """
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
pipeline {
    agent {
        docker {
            image 'mcr.microsoft.com/playwright/java:v1.58.0-noble'
            args '--ipc=host -v /var/jenkins_home/.m2:/root/.m2'
        }
    }

    tools {
        allure 'Allure 2.30'
    }

    parameters {
        text(
                name: 'CONFIG',
                defaultValue: '''browser: chromium
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
                script {
                    def cfg = readYaml text: params.CONFIG
                    env.BROWSER = cfg.browser?.toString()?.trim()
                    env.BASE_URL = cfg.base_url?.toString()?.trim()
                    env.REMOTE = cfg.remote?.toString()?.trim()

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
            archiveArtifacts artifacts: 'allure-results/**, target/allure-results/**', allowEmptyArchive: true

            allure([
                    includeProperties: false,
                    jdk: '',
                    properties: [],
                    reportBuildPolicy: 'ALWAYS',
                    results: [
                            [path: 'allure-results'],
                            [path: 'target/allure-results']
                    ]
            ])
        }
    }
}
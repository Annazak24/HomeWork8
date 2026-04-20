node('maven') {
    try {
        stage('Checkout') {
            checkout scm
        }

        stage('Prepare config') {
            writeFile file: 'config.yaml', text: params.CONFIG

            env.BASE_URL = sh(
                    script: "grep '^base_url:' config.yaml | cut -d':' -f2- | xargs",
                    returnStdout: true
            ).trim()

            env.ENVIRONMENT = sh(
                    script: "grep '^environment:' config.yaml | cut -d':' -f2- | xargs",
                    returnStdout: true
            ).trim()

            echo "Base URL: ${env.BASE_URL}"
            echo "Environment: ${env.ENVIRONMENT}"
        }

        stage('Run API Tests in Docker') {
            sh """
                rm -rf allure-results
                mkdir -p allure-results

                docker build -t api-tests .

                docker run --rm \
                  -v "\${WORKSPACE}/allure-results:/app/allure-results" \
                  api-tests \
                  mvn clean test \
                    -Dbase.url=${env.BASE_URL} \
                    -Denvironment=${env.ENVIRONMENT} \
                    -Dmaven.test.failure.ignore=true \
                    -Dallure.results.directory=/app/allure-results
            """
        }
    } finally {
        stage('Archive Allure Results') {
            archiveArtifacts artifacts: 'allure-results/**', allowEmptyArchive: true
        }

        stage('Allure Report') {
            allure([
                    includeProperties: false,
                    jdk: '',
                    properties: [],
                    reportBuildPolicy: 'ALWAYS',
                    results: [[path: 'allure-results']]
            ])
        }
    }
}
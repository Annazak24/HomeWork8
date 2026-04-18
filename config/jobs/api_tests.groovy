node('maven') {

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

    stage('Run API Tests') {
        sh """
            mvn clean test \
              -Dbase.url=${env.BASE_URL} \
              -Denvironment=${env.ENVIRONMENT}
        """
    }
}
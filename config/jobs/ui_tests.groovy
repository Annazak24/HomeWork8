node('maven') {

    stage('Checkout') {
        checkout scm
    }

    stage('Prepare config') {
        writeFile file: 'config.yaml', text: params.CONFIG

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

    stage('Run UI Tests') {
        sh """
            mvn clean test \
              -Dbrowser=${env.BROWSER} \
              -Dbase.url=${env.BASE_URL} \
              -Dremote=${env.REMOTE}
        """
    }
}
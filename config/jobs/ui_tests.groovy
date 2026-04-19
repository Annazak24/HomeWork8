stage('Prepare config') {
    steps {
        script {
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
        }
    }
}
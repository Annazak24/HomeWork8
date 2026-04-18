node('maven') {

    def JOBS_DIR = "${env.WORKSPACE}/config/jobs"
    def CONFIG_FILE = "${env.WORKSPACE}/uploader_config.ini"

    stage('Checkout') {
        checkout scm
    }

    stage('Create init conf') {
        withCredentials([usernamePassword(
                credentialsId: 'uploader',
                passwordVariable: 'pass',
                usernameVariable: 'user'
        )]) {
            withEnv([
                    "CONFIG_FILE=${CONFIG_FILE}",
                    "JOBS_DIR=${JOBS_DIR}"
            ]) {
                sh '''
                    cat > "$CONFIG_FILE" <<EOF
[jenkins]
url=http://jenkins:8080/my_jenkins/
user=$user
password=$pass

[job_builder]
recursive=True
keep_descriptions=False
EOF
                '''
            }
        }
    }

    stage('Run JJB in Docker') {
        withEnv([
                "CONFIG_FILE=${CONFIG_FILE}",
                "JOBS_DIR=${JOBS_DIR}"
        ]) {
            script {
                docker.image('jenkins-jjb:latest').inside {
                    sh '''
                        jenkins-jobs --version
                        ls -la
                        ls -la "$JOBS_DIR"
                        cat "$CONFIG_FILE"
                        jenkins-jobs --conf "$CONFIG_FILE" --flush-cache update "$JOBS_DIR"
                    '''
                }
            }
        }
    }
}
node('maven') {

    def JOBS_DIR = "${env.WORKSPACE}/config/jobs"
    def CONFIG_FILE = "${env.WORKSPACE}/uploader.ini"

    stage('Checkout') {
        checkout scm
    }

    stage('Create init conf') {
        withCredentials([usernamePassword(
                credentialsId: 'uploader',
                passwordVariable: 'pass',
                usernameVariable: 'user'
        )]) {
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

    stage('Debug') {
        sh '''
            which jenkins-jobs || true
            jenkins-jobs --version || true
            ls -la
            ls -la "$JOBS_DIR" || true
            cat "$CONFIG_FILE"
        '''
    }

    stage('Upload jobs') {
        sh '''
            jenkins-jobs --conf "$CONFIG_FILE" --flush-cache update "$JOBS_DIR"
        '''
    }
}
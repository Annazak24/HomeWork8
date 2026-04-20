pipeline {
    agent any

    parameters {
        choice(
                name: 'TEST_TYPE',
                choices: ['all', 'api', 'ui', 'mobile'],
                description: 'Select which test suite to run'
        )
    }

    stages {
        stage('Run selected jobs') {
            steps {
                script {
                    def jobs = [:]

                    if (params.TEST_TYPE == 'all' || params.TEST_TYPE == 'api') {
                        jobs['API_Tests'] = {
                            build job: 'api_tests', propagate: false, wait: true
                        }
                    }

                    if (params.TEST_TYPE == 'all' || params.TEST_TYPE == 'ui') {
                        jobs['UI_Tests'] = {
                            build job: 'ui_tests', propagate: false, wait: true
                        }
                    }

                    if (params.TEST_TYPE == 'all' || params.TEST_TYPE == 'mobile') {
                        jobs['Mobile_Tests'] = {
                            build job: 'mobile_tests', propagate: false, wait: true
                        }
                    }

                    if (jobs.isEmpty()) {
                        error('No jobs selected')
                    }

                    parallel jobs
                }
            }
        }
    }
}
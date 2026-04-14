node('maven') {

    stage('Checkout') {
        checkout scm
    }

    stage('Run UI Tests') {
        sh 'mvn clean test'
    }

}
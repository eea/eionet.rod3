pipeline {
  agent {
    node { label "docker-host" }
  }

  environment {
    GIT_NAME = "eionet.rod3"
  }
  
  stages {


    stage ('Build & Docker push') {
      when {
          environment name: 'CHANGE_ID', value: ''
      }
      tools {
         maven 'maven3.9'
         jdk 'Java11'
      }
      steps {
        script {
          withCredentials([string(credentialsId: 'eea-jenkins-token', variable: 'GITHUB_TOKEN'),  usernamePassword(credentialsId: 'jekinsdockerhub', usernameVariable: 'DOCKERHUB_USER', passwordVariable: 'DOCKERHUB_PASS')]) {
                sh '''mkdir -p ~/.m2'''
                sh '''find /var/jenkins_home/worker/tools -type d -name .m2 2>/dev/null'''
                sh ''' sed "s/TOKEN/ersRnmSYSzDQfCK4lARVnSWxNbw67T2qBCvN/" m2.settings.tpl.xml > ~/.m2/settings.xml '''
                sh '''mvn -Ddocker.username=$DOCKERHUB_USER -Ddocker.password=$DOCKERHUB_PASS -Pdocker clean install docker:push '''
            }
         }
       }
     }
  }

  post {
      always {
        cleanWs(cleanWhenAborted: true, cleanWhenFailure: true, cleanWhenNotBuilt: true, cleanWhenSuccess: true, cleanWhenUnstable: true, deleteDirs: true)
        script {
          def url = "${env.BUILD_URL}/display/redirect"
          def status = currentBuild.currentResult
          def subject = "${status}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'"
          def summary = "${subject} (${url})"
          def details = """<h1>${env.JOB_NAME} - Build #${env.BUILD_NUMBER} - ${status}</h1>
                           <p>Check console output at <a href="${url}">${env.JOB_BASE_NAME} - #${env.BUILD_NUMBER}</a></p>
                        """

          def color = '#FFFF00'
          if (status == 'SUCCESS') {
            color = '#00FF00'
          } else if (status == 'FAILURE') {
            color = '#FF0000'
          }
          
          withCredentials([string(credentialsId: 'eworx-email-list', variable: 'EMAIL_LIST')]) {
                   emailext(
                   to: "$EMAIL_LIST",
                   subject: '$DEFAULT_SUBJECT',
                   body: details,
                   attachLog: true,
                   compressLog: true,
                  )
           }
        }
      }
  }
}

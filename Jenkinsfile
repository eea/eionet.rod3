pipeline {
  agent {
    node { label "docker-host" }
  }

  environment {
    GIT_NAME = "eionet.rod3"
    registry = "eeacms/rod"
    dockerImage = ''
    tagName = ''
  }
  tools {
    maven 'maven3.9'
    jdk 'Java11'
  }

  stages {

    stage ('Build & Test') {
      when {
          environment name: 'CHANGE_ID', value: ''
      }

      steps {
        script {
          withCredentials([string(credentialsId: 'jenkins-maven-token', variable: 'GITHUB_TOKEN')]) {
            sh '''mkdir -p ~/.m2'''
            sh '''sed "s/TOKEN/$GITHUB_TOKEN/" m2.settings.tpl.xml > ~/.m2/settings.xml '''
            sh '''mvn clean verify pmd:pmd spotbugs:spotbugs'''
          }
        }
       }
       post {
         always {
             junit 'target/surefire-reports/*.xml, target/failsafe-reports/*.xml'
             recordCoverage(tools: [[parser: 'JACOCO']],
               id: 'jacoco', name: 'JaCoCo Coverage',
               sourceCodeRetention: 'EVERY_BUILD',
               ignoreParsingErrors: true,
               qualityGates: [
                  [threshold: 5.0, metric: 'LINE', baseline: 'PROJECT', unstable: true],
                  [threshold: 5.0, metric: 'BRANCH', baseline: 'PROJECT', unstable: true]])
             publishHTML target:[
                allowMissing: false,
                alwaysLinkToLastBuild: false,
                keepAll: true,
                reportDir: 'target/site/jacoco',
                reportFiles: 'index.html',
                reportName: "Detailed Coverage Report"
             ]
         }
       }
    }

    stage ('Build & Docker push') {
      when {
        environment name: 'CHANGE_ID', value: ''
        anyOf {
          branch 'master'
          branch 'develop'
        }
      }
      steps {
        script {
          withCredentials([string(credentialsId: 'jenkins-maven-token', variable: 'GITHUB_TOKEN'),  usernamePassword(credentialsId: 'jekinsdockerhub', usernameVariable: 'DOCKERHUB_USER', passwordVariable: 'DOCKERHUB_PASS')]) {
            sh '''mkdir -p ~/.m2'''
            sh ''' sed "s/TOKEN/$GITHUB_TOKEN/" m2.settings.tpl.xml > ~/.m2/settings.xml '''
            try {
              sh '''mvn -Dmaven.test.skip=true -Ddocker.username=$DOCKERHUB_USER -Ddocker.password=$DOCKERHUB_PASS -Pdocker clean verify '''

              if (env.BRANCH_NAME == 'master') {
                tagName = 'latest'
              } else {
                tagName = "$BRANCH_NAME"
              }
              def date = sh(returnStdout: true, script: 'echo $(date "+%Y-%m-%dT%H%M")').trim()
              dockerImage = docker.build("$registry:$tagName", "--no-cache .")
              docker.withRegistry( '', 'eeajenkins' ) {
                dockerImage.push()
                dockerImage.push(date)
              }
            } finally {
              sh '''mvn dependency:purge-local-repository -DactTransitively=false -DreResolve=false'''
            }
          }
        }
      }
      post {
        always {
          sh "docker rmi $registry:$tagName | docker images $registry:$tagName"
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

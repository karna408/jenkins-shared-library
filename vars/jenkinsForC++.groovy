def call(String repoUrl){
  pipeline{
    agent any
 
    /*-------- CODE CLONE ------------ */
    stages{
        stage('git pull'){
            steps{
             sh 'rm -rf $WORKSPACE/*'
            checkout ([$class: 'GitSCM', branches: [[name: '*/master']],
                       userRemoteConfigs: [[url: 'https://github.com/karna408/jenkins-cmake-gtest-project2.git']],
                ])
            }
        }
        /*----------------------Code Coverage Stage-------------------*/
        stage('Code Coverage'){
            steps{
                sh '''
                  cd $WORKSPACE
                  sh coco.sh
                   '''
            }
        }
       
        /*---------------------------Sonarqube SCA Stage-----------------*/
        stage('Static Code Analysis'){
            steps{
                withSonarQubeEnv('Sonar'){
                    sh "sonar-scanner -Dsonar.projectKey=sample-lib -Dsonar.sources=."
                }
            }
        }
        /*--------------------------- Build Stage ----------------------- */
        stage('Build'){
            steps{
                sh '''
                  cd $WORKSPACE
                  sh build.sh
                  '''
             }
        }
        /*    ----------------- Test Stage ------------------- */
        stage('Test'){
            steps{
                 sh '''
                    cd $WORKSPACE
                    sh test.sh
                 '''
              }
       }

     }
     post {
        always {
            archiveArtifacts artifacts: 'test/build/app-sample-addition-test', fingerprint: true
            
        }
    }
  }
}


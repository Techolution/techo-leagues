node {
   stage 'checkout'

   // Get some code from a GitHub repository
   git url: 'https://github.com/techolution-ex/techo-leagues.git'
   sh 'git clean -fdx; sleep 4;'

   def mvnHome = tool 'mvn'

   stage 'build'
   sh "cd config-server"
   sh "${mvnHome}/bin/mvn versions:set -DnewVersion=${env.BUILD_NUMBER}"
   sh "${mvnHome}/bin/mvn package"
   
   
   }

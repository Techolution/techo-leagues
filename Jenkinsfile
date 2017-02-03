node {
   stage 'checkout'

   // Get some code from a GitHub repository
   git url: 'https://github.com/techolution-ex/techo-leagues.git'
   sh "git clean -f && git reset --hard origin/karthik_projectfix_tdd"

   def mvnHome = tool 'mvn'

   stage 'build'
   
   echo "============================================================================================"
   //sh "${mvnHome}/bin/mvn versions:set -DnewVersion=${env.BUILD_NUMBER}"
   cd ./server-config
   sh "pwd"
   }

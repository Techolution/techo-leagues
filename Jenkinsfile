node {
   stage 'checkout'

   // Get some code from a GitHub repository
   git url: 'https://github.com/techolution-ex/techo-leagues.git'
   sh "git clean -f && git reset --hard origin/karthik_projectfix_tdd"

   def mvnHome = tool 'mvn'

   stage 'build'
   sh "set +e"
   def workspace = pwd() 
   sh "cd ${workspace}/server-config"
   sh "${mvnHome}/bin/mvn versions:set -DnewVersion=${env.BUILD_NUMBER}"
   sh "${mvnHome}/bin/mvn -f  /server-config/pom.xml package"
   
   
   }

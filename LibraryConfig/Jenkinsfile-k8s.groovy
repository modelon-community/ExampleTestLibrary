podTemplate(yaml: """
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: mtt
    image: <Modelon ACR>
    command: ["sleep","3600"]
  imagePullSecrets:
  - name: <acrsecret>
""",
label: "jenkins-slave",
//volumes: [persistantVolumeClaim]
  ) {
    node("jenkins-slave") {
      container('mtt') {
        def workspace = pwd()
        stage('Checkout') {
          sh "svn checkout https://github.com/modelon-community/ExampleTestLibrary.git/trunk ExampleTestLibrary"
          // Obtain License: either clone from a repo or create a client .lic file. Replace <..> with actual values.
          sh """
            licfile = $workspace/client.lic
            echo "SERVER <host_name> ANY <port>" > \$licfile
            echo "VENDOR modelon" >> \$licfile
            echo "USE_SERVER" >> \$licfile
          	export MODELON_LICENSE_PATH=\$licfile
          """
        }

        stage('Run & Verify') {
          dir("Results") {
              deleteDir()
          }
          echo "Build info of current Docker image:"
          sh"""
              cat /usr/bin/mtt/build_info.txt
              rpm -q modelon-mtt
              rpm -q modelon-oct
              export PYTHONPATH=:/usr/bin/mtt/mtt-3.0.0-py3.6.egg/::\$PYTHONPATH
              mkdir temp
              cd temp
              /usr/bin/oct/install/bin/jm_python.sh -m mtt configure $workspace/ExampleTestLibrary/LibraryConfig/test_mtt_default.yaml
              /usr/bin/oct/install/bin/jm_python.sh -m mtt run verify
          """
        }
      
        stage("Archive") {
          dir ("Results/Output") {
              archiveArtifacts artifacts: '**'
          }
          junit 'Results/Output/**/*.xml'
          currentBuild.description = "<a href=\"${env.BUILD_URL}/artifact/index.html\">Click here for HTML report</a>"
        }
      }
    }
  }

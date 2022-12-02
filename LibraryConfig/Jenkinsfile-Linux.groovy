def mttImage = "/home/jenkins/images/mtt_oct_stable.tar"
def mttImageName = "artifactory.modelon.com/mtt.oct.images/mtt_oct_stable_centos:latest"

//def yaml_file="..\\ExampleTestLibrary\\LibraryConfig\\test_mtt_default.yaml"

node("Linux") {        
stage('Checkout') {
    dir("ExampleTestLibrary") {
        checkout scm // defaults work nicely for git
    }
}
try {
    stage('Test') {
        sh "docker load -i ${mttImage}"
        def testingImage = docker.image(mttImageName)
        def workspace = pwd()

        testingImage.inside("-v ${workspace}:/job -w /RunDirectory") {
            sh'''
                export PYTHONPATH=:/usr/bin/mtt/mtt-3.0.0-py3.6.egg/::$PYTHONPATH
                /usr/bin/oct/install/bin/jm_python.sh -m mtt configure /job/ExampleTestLibrary/LibraryConfig/test_mtt_default.yaml
                /usr/bin/oct/install/bin/jm_python.sh -m mtt run verify
            '''
        }

    }
    } finally {
        archiveArtifacts artifacts: 'Results/Output/**/*.*'
        archiveArtifacts artifacts: 'Results/SimulationResult/**', allowEmptyArchive: true
        junit 'Results/Output/**/*.xml'
        currentBuild.description = " <a href=\"${env.BUILD_URL}/artifact/Results/Output/index.html\">Click here for HTML report</a>"
    }
}
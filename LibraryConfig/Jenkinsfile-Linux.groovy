def mttImageName = "artifactory.modelon.com/aid.docker.dev.local/mtt/stable:latest"

properties([pipelineTriggers([cron('H 6 * * 7')])])  // Just to ensure that it get run once a week

node("Linux") {        
    stage('Checkout') {
        dir("ExampleTestLibrary") {
            checkout scm // defaults work nicely for git
        }
    }
    try {
        stage('Test') {
            sh "docker pull ${mttImageName}"
            def testingImage = docker.image(mttImageName)
            def workspace = pwd()

            dir("Results") {
                deleteDir()
            }
            dir("RunDirectory") {
                deleteDir()
            }

            testingImage.inside("-v ${workspace}:/job -w /RunDirectory") {
            echo "Build info of current Docker image:"
                sh'''
                    cat /usr/bin/mtt/build_info.txt
                    apt show mtt
                    cat /usr/bin/oct/install/version.txt

                    export PYTHONPATH=:/usr/bin/mtt/mtt-3.0.0-py3.9.egg/::$PYTHONPATH
                    /usr/bin/mtt/mtt.sh configure /job/ExampleTestLibrary/LibraryConfig/test_mtt_default.yaml
                    /usr/bin/mtt/mtt.sh run verify
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

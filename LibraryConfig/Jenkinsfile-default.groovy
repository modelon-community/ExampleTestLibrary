def oct_sdk_home="C:\\OCT-SDK-1.6"
def oct_home="C:\\ModelonSW\\OCT"
def mtt_home="C:\\ModelonSW\\MTT\\mtt-4.2.7-py3.7.egg" 
def yaml_file="..\\ExampleTestLibrary\\LibraryConfig\\test_mtt_default.yaml"

properties([pipelineTriggers([cron('H 6 * * 7')])])  // Just to ensure that it get run once a week

node("Windows") {        
    stage('Checkout') {
        dir("ExampleTestLibrary") {
            checkout scm // defaults work nicely for git
        }
    }
    try {
        stage('Test') {
            dir("Results") {
                deleteDir()
            }
            dir("RunDirectory") {
                deleteDir()
            }

            dir("RunDirectory") {
                bat """
set OCT_HOME=${oct_home}
call ${oct_sdk_home}/setenv.bat
echo on
set MTT_HOME=${mtt_home}
set PATH=%MTT_HOME%;%PATH%
set PYTHONPATH=%MTT_HOME%;%PYTHONPATH%;
call python -m mtt configure ${yaml_file}
call python -m mtt run verify
                """
            }   
        }
    } finally {
        archiveArtifacts artifacts: 'Results/Output/**/*.*'
        archiveArtifacts artifacts: 'Results/SimulationResult/**', allowEmptyArchive: true
        junit 'Results/Output/**/*.xml'
        currentBuild.description = " <a href=\"${env.BUILD_URL}/artifact/Results/Output/index.html\">Click here for HTML report</a>"
    }
}

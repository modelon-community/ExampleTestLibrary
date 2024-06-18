def oct_sdk_home="C:\\OCT-SDK-1.7.1"
def oct_home="C:\\ModelonSW\\OCT"
def mtt_home="C:\\ModelonSW\\MTT" 
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
call python -m venv --system-site-packages venv
call .\\venv\\Scripts\\activate.bat
call python -m pip install ruamel.yaml==0.17.21
call %MTT_HOME%\\mtt configure ${yaml_file}
call %MTT_HOME%\\mtt run create_reference
                """
            }   
        }
    } finally {
        archiveArtifacts artifacts: 'Results/Output/**/*.*'
        archiveArtifacts artifacts: 'ExampleTestLibrary/ReferenceFiles/**/*.*'
        junit 'Results/Output/**/*.xml'
        currentBuild.description = " <a href=\"${env.BUILD_URL}/artifact/Results/Output/index.html\">Click here for HTML report</a>"
    }
}

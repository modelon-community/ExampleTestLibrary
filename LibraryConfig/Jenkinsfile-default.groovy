def oct_home="C:\\OCT-SDK-1.5"
def mtt_home="C:\\ModelonSW\\MTT\\mtt-3.0.0-py3.7.egg" 
def yaml_file="..\\ExampleTestLibrary\\LibraryConfig\\test_mtt_default.yaml"

mttpath = mtt_home
node("Windows") {        
stage('Checkout') {
    dir("ExampleTestLibrary") {
        checkout scm // defaults work nicely for git
    }
}
try {
    stage('Test') {
        dir("RunDirectory") {
        bat """
        call ${oct_home}/setenv.bat
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
        junit 'Results/Output/**/*.xml'
        currentBuild.description = " <a href=\"${env.BUILD_URL}/artifact/Results/Output/index.html\">Click here for HTML report</a>"
    }
}
def oct_home="C:\\OCT-SDK-1.5"
def mtt_home="C:\\ModelonSW\\MTT\\mtt-3.0.0-py3.7.egg" 
def yaml_file="ExampleTestLibrary\\LibraryConfig\\test_mtt_default.yaml"

mttpath = mtt_home
node("Windows") {        
stage('Checkout') {
    dir("Models") {
        checkout scm // defaults work nicely for git
    }
}
try {
        stage('Test') {
            dir("Run") {
            bat """
            call ${oct_home}/setenv.bat
            set MTT_HOME=${mtt_home}
            set PATH=%MTT_HOME%;%PATH%
            set PYTHONPATH=%MTT_HOME%;%PYTHONPATH%;%MTT_HOME%\\${mtt_egg}

                ${mttpath}/mtt.bat ${WORKSPACE}/Models/${yaml_file}”
            """
            }
        }
        } finally {
            archiveArtifacts artifacts: 'Output/**/*.*'
            junit 'Output/**/*.xml'
        }
}

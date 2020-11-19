def oct_home="C:\\ModelonSW\\OCT"
def mtt_home="C:\\ModelonSW\\MTT"
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

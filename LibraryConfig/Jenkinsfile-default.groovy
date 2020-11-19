def oct_home="C:\\programs\\OCT\\edinburgh\\stable"
def mtt_home="C:\\Users\\JesseGohl\\projects\\P539-MTT\\Installations"
def yaml_file="TestingMTT\\LibraryConfig\\test_mtt_default.yaml"

mttpath = where mtt is
node() {        
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
            archiveArtifacts artifacts: 'Outputs/**/*.*'
            junit 'Outputs/**/*.xml'
        }
}

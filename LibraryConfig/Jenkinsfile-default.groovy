import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;
import org.yaml.snakeyaml.Yaml;
import java.nio.file.Path;
import java.nio.file.Paths;


// Only keep the last five builds (for space reasons)
properties([[$class: 'BuildDiscarderProperty',
	strategy: [
		$class: 'LogRotator',
	artifactNumToKeepStr: '50']]])
	
	// Includes build cause in display name
	currentBuild.displayName += " (" +
	(env.TRIGGER_CAUSE == null ? "MANUAL" : env.TRIGGER_CAUSE) +
	")"
	node {
		def yamlPaths = [:]
		def baseDir = pwd()
	
		// The following must be customized for the tests and build environment
		def oct_home="C:\\programs\\OCT\\edinburgh\\stable"
		def mtt_home="C:\\Users\\JesseGohl\\projects\\P539-MTT\\Installations"
		def mtt_egg="mtt-3.0.0-py3.7.egg"
		def repo_path = 'https://github.com/jbgohl/JenkinsTest.git' 
		def yaml_file="ExampleTestLibrary/LibraryConfig/test_mtt_default.yaml"
		
		stage("Setup") {
			// Checkout run configuration
			doCheckout(repo_path, 'master', 'Runconfigs', '')
			
			// Find what to checkout, parse the run configuration
			def cfgPaths = readYaml file: yaml_file
			yamlPaths = cleanPaths(baseDir, cfgPaths)

			// Convert the tests
			def scriptToRun = generateConvBatCall(baseDir, oct_home, mtt_home, mtt_egg, yaml_file)
			bat scriptToRun
		}
		
		stage("Test") {
			// Run the tests
			def scriptToRun = generateTestBatCall(baseDir, oct_home, mtt_home, mtt_egg)
			bat scriptToRun
		}
		
		stage("Archive") {	
			// Get the result paths
			def mypaths = archive_paths(baseDir, yamlPaths['output-dir'].toString())
			def outpath = mypaths['out-path'].replace('\\', '/')

			// Archive the results
			step([$class: 'JUnitResultArchiver', testResults: mypaths['xml-path']])
			step([$class: 'ArtifactArchiver', artifacts: mypaths['out-files'], fingerprint: true])
			
			// Add a link to the report
			currentBuild.description = """ <a href="artifact/${outpath}/index.html">Click here for HTML results</a> """
		}
	}
		

	@NonCPS
	def cleanPaths(baseDir, cfgPaths) {
		// Helper function to extract directories from yaml config
		def mapLocalPaths = [:]		
		def thisDirStr = ""
		def thisDir = ""		
		def mttResDirStr = ""
		def mttResDir = ""	
		def outDirStr = ""
		def outDir = ""		
	
		thisDirStr = cfgPaths['MTT']['this-directory']
		mttResDirStr = cfgPaths['MTT']['mtt-result-dir']
		outDirStr = cfgPaths['MTT']['output-dir']
		
		if (thisDirStr == null || thisDirStr.isEmpty()) {
			thisDir = Paths.get(baseDir + "/../Results").normalize().toString()
		} else {
			thisDir = Paths.get(baseDir + "/" + thisDirStr).normalize().toString()			
		}

		mapLocalPaths['this-dir'] = thisDir		
		
		if (mttResDirStr == null || mttResDirStr.isEmpty()) {
			mttResDir = Paths.get(thisDir + "/../Results").normalize().toString()
		} else {
			mttResDir = Paths.get(baseDir + "/" + mttResDirStr	).normalize().toString()			
		}

		mapLocalPaths['mtt-result-dir'] = mttResDir		
		
		if (outDirStr == null || outDirStr.isEmpty()) {
			outDir = Paths.get(mttResDir + "/Output").normalize().toString()
		} else {
			outDir = Paths.get(baseDir + "/" + outDirStr).normalize().toString()
		}
		
		mapLocalPaths['output-dir'] = outDir

		return mapLocalPaths
	}
	


	def generateConvBatCall(baseDir, oct_home, mtt_home, mtt_egg, yaml_file) {
		// Helper function to generate .bat script used to run tests
		StringBuffer sb = new StringBuffer();		
		sb = generateBatSetup(oct_home, mtt_home, mtt_egg)
		sb.append("call python -m mtt configure ")
		sb.append("${baseDir}")
		sb.append("\\")
		sb.append("${yaml_file}")
		sb.append("\n")
		return sb.toString()
	}
	
	
	def generateTestBatCall(baseDir, oct_home, mtt_home, mtt_egg) {
		// Helper function to generate .bat script used to run tests
		StringBuffer sb = new StringBuffer();
		sb = generateBatSetup(oct_home, mtt_home, mtt_egg)
		sb.append("call python -m mtt run verify \n")
		return sb.toString()
	}
	
	
	def generateBatSetup(oct_home, mtt_home, mtt_egg) {
		// Helper function to generate .bat script used to run tests
		StringBuffer sb = new StringBuffer();
		sb.append("call ")
		sb.append("${oct_home}")
		sb.append("\\setenv.bat\n")
		sb.append("set \"MTT_HOME=");
		sb.append("${mtt_home}")
		sb.append("\n");
		sb.append("set \"PATH=%MTT_HOME%;%PATH%\"\n");
		sb.append("set \"PYTHONPATH=%MTT_HOME%;%PYTHONPATH%;%MTT_HOME%\\");
		sb.append("${mtt_egg}")
		sb.append("\n");
		sb.append("set \"PYTHON_PATH=%PYTHONHOME%\"\n");
		return sb
	}
	
	
	@NonCPS
	def archive_paths(baseDir, outDir) {
		// Helper function to generate paths needed for output reports
		def mypaths = [:]
		def tmpPath = Paths.get(outDir,"results.xml")
		def fullPath = Paths.get(baseDir)
		def relPath = fullPath.relativize(tmpPath.normalize()).toString()
		
		mypaths['xml-path'] = relPath
		
		tmpPath = Paths.get(outDir)
		relPath = fullPath.relativize(tmpPath.normalize()).toString()
		
		mypaths['out-path'] = relPath
		mypaths['out-files'] = relPath + "\\**\\*.*"
		
		return mypaths
	}
	
	
	@NonCPS
	def doCheckout(remoteLoc, branch, localLoc, credentials = '') {
		// Helper function that performs a checkout from Git
		echo "Checking out ${remoteLoc} to ${localLoc}"
		checkout([$class: 'GitSCM', branches: [[name: '*/' + branch]],
			userRemoteConfigs: [[url: remoteLoc]]])
	}	
	
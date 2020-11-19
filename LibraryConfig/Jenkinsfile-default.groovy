import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;
import org.yaml.snakeyaml.Yaml;


// Only keep the last 50 builds (for space reasons)
properties([[$class: 'BuildDiscarderProperty',
	strategy: [
		$class: 'LogRotator',
	artifactNumToKeepStr: '50']]])
	
	// Includes build cause in display name
	currentBuild.displayName += " (" +
	(env.TRIGGER_CAUSE == null ? "MANUAL" : env.TRIGGER_CAUSE) +
	")"
	node("Windows") {
		def baseDir = pwd()
	
		// The following must be customized for the tests and build environment 
		def mtt_egg="mtt-3.0.0-py3.7.egg"
		def repo_path = "https://github.com/jesse-gohl/ExampleTestLibrary.git" 
		def yaml_file="LibraryConfig/test_mtt_default.yaml"
		
		stage("Setup") {
			// Checkout run configuration
			//doCheckout(repo_path, 'master', 'Runconfigs', '')			
            checkout scm
			// Convert the tests
			def scriptToRun = generateConvBatCall(baseDir, mtt_egg, yaml_file)
			bat scriptToRun
		}
		
		stage("Test") {
			// Run the tests
			def scriptToRun = generateTestBatCall(baseDir, mtt_egg)
			bat scriptToRun
		}
		
		stage("Archive") {	
			// Get the result paths
			// Find what to checkout, parse the run configuration
			def cfgPaths = readYaml file: yaml_file
			def yamlPaths = cleanPaths(baseDir, cfgPaths)
			
			// Archive the results
			dir (yamlPaths['output-dir']) {
				step([$class: 'JUnitResultArchiver', testResults: "results.xml"])
				step([$class: 'ArtifactArchiver', artifacts: "*.*", fingerprint: true])
			}
			// Add a link to the report
			currentBuild.description = """ <a href="artifact/index.html">Click here for HTML results</a> """
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
			thisDir = baseDir + "/../Results"
		} else {
			thisDir = baseDir + "/" + thisDirStr
		}

		mapLocalPaths['this-dir'] = thisDir		
		
		if (mttResDirStr == null || mttResDirStr.isEmpty()) {
			mttResDir = thisDir + "/../Results"
		} else {
			mttResDir = baseDir + "/" + mttResDirStr
		}

		mapLocalPaths['mtt-result-dir'] = mttResDir		
		
		if (outDirStr == null || outDirStr.isEmpty()) {
			outDir = mttResDir + "/Output"
		} else {
			outDir = baseDir + "/" + outDirStr
		}
		
		mapLocalPaths['output-dir'] = outDir

		return mapLocalPaths
	}
	

	def generateConvBatCall(baseDir, mtt_egg, yaml_file) {
		// Helper function to generate .bat script used to run tests
		StringBuffer sb = new StringBuffer();		
		sb = generateBatSetup(mtt_egg)
		sb.append("call python -m mtt configure ")
		sb.append("${baseDir}")
		sb.append("\\")
		sb.append("${yaml_file}")
		sb.append("\n")
		return sb.toString()
	}
	
	
	def generateTestBatCall(baseDir, mtt_egg) {
		// Helper function to generate .bat script used to run tests
		StringBuffer sb = new StringBuffer();
		sb = generateBatSetup(mtt_egg)
		sb.append("call python -m mtt run verify \n")
		return sb.toString()
	}
	
	
	def generateBatSetup(mtt_egg) {
		// Helper function to generate .bat script used to run tests
		StringBuffer sb = new StringBuffer();	
		sb.append("set \"PATH=%MTT_HOME%;%PATH%\"\n");
		sb.append("set \"PYTHONPATH=%MTT_HOME%;%PYTHONPATH%;%MTT_HOME%\\");
		sb.append("${mtt_egg}")
		sb.append("\n");
		sb.append("set \"PYTHON_PATH=%PYTHONHOME%\"\n");
		return sb
	}
	
	
	@NonCPS
	def doCheckout(remoteLoc, branch, localLoc, credentials = '') {
		// Helper function that performs a checkout from Git
		echo "Checking out ${remoteLoc} to ${localLoc}"
		checkout([$class: 'GitSCM', branches: scm.branches,
			userRemoteConfigs: [[url: remoteLoc]]])
	}	
	
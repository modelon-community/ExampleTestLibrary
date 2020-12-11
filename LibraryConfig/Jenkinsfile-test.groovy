import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;


// Only keep the last 50 builds (for space reasons)
properties([[$class: 'BuildDiscarderProperty',
	strategy: [
		$class: 'LogRotator',
	artifactNumToKeepStr: '50']]])
	
	// Includes build cause in display name
	currentBuild.displayName += " (" +
	(env.TRIGGER_CAUSE == null ? "MANUAL" : env.TRIGGER_CAUSE) +
	")"
	node {
		def baseDir = pwd()
	
		// The following must be customized for the tests and build environment 
		def repo_path = "git@github.com/modelon-community/ExampleTestLibrary.git"   
		
		stage("Setup") {
			// Checkout run configuration
			echo "Hello world"
			doCheckout(repo_path, 'main')
		}		
	}
		
	
	@NonCPS
	def doCheckout(remoteLoc, branch) {
		// Helper function that performs a checkout from Git
		echo "Checking out ${remoteLoc}"
		checkout([$class: 'GitSCM', branches: [[name: '*/' + branch]],
			userRemoteConfigs: [[url: remoteLoc]]])
	}	
	
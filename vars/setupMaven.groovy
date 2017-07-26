#!/usr/bin/groovy

/**
 * Setups maven in the target namespace:
 * 1. install maven settings.xml
 * 2. install persistent local maven reposiory (TODO: This might be overkill, we need to check if it make sense)
 * 3. install the jenkins service account.
 * @param parameters Parameters to customize the Maven container.
 * @param body The code to wrap.
 * @return
 */
def call(Map parameters = [:]) {

	def defaultLabel = buildId('maven')
	def label = parameters.get('label', defaultLabel)
	def name = parameters.get('name', 'maven')

	def cloud = parameters.get('cloud', 'openshift')
	def namespace = parameters.get('namespace', "${KUBERNETES_NAMESPACE}")
	def manifestLocation = parameters.get('manifestLocation', '')
	def readFromWorkspace = parameters.get('readFromWorkspace', false)

	if (readFromWorkspace) {
	  manifestLocation = "file:${WORKSPACE}/manifests"
	} else if(manifestLocation.isEmpty()) {
	  manifestLocation = 'https://raw.githubusercontent.com/syndesisio/syndesis-pipeline-library/master/manifests'
	}


        echo "Setting maven in namespace: ${namespace} using manifests from: ${manifestLocation}."
	createEnvironment(cloud: "${cloud}", name: "${namespace}",
			environmentDependencies: [
					"${manifestLocation}/m2-settings-secret.yml",
					"${manifestLocation}/jenkins-sa.yml"]  ,
			namespaceDestroyEnabled: false,
			namespaceCleanupEnabled: false,
			waitTimeout: 600000L)
}

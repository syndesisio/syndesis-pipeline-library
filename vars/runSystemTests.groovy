#!/usr/bin/groovy

def call(Map parameters = [:]) {

    def component = parameters.get('component')
    def version = parameters.get('version', '1.0')
    def key = "${component}_VERSION".toUpperCase().replace('-', '_')
    def defaultNamespace = "systest-${version}"
    def namespace = parameters.get('namespace', defaultNamespace)
    def parametersFilePath = "parameters.yml";
    
    container(name: 'openshift') {
            //copy the oc binary to the project workspace
            sh 'cp $(which oc) $(pwd)'
        }

        container(name: 'maven') {
            sh "mvn clean install -U -Dnamespace.use.existing=${namespace}"
        }
}

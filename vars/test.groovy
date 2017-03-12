#!/usr/bin/groovy

/**
 * Runs the system test suite.
 * @param parameters
 * @return
 */
def call(Map parameters = [:]) {

    def component = parameters.get('component')
    def version = parameters.get('version', '1.0')
    def defaultNamespace = "systest-${version}"
    def namespace = parameters.get('namespace', defaultNamespace)

    container(name: 'openshift') {
        sh """
        mkdir -p \${HOME}/bin
        cp \$(which oc) \${HOME}/bin/
        """
    }

    container(name: 'maven') {
        git 'https://github.com/redhat-ipaas/ipaas-system-tests.git'
        sh """
        mkdir -p \${HOME}/bin
        export PATH=\${PATH}:\${HOME}/bin
        mvn clean install -U -Dnamespace.use.existing=${namespace}
        """
    }
}

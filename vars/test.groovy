#!/usr/bin/groovy

/**
 * Runs the system test suite.
 * @param parameters
 * @return
 */
def call(Map parameters = [:]) {

    def namespace = parameters.get('namespace', '')

    container(name: 'openshift') {
        sh """
        mkdir -p \${HOME}/bin
        cp \$(which oc) \${HOME}/bin/
        """
    }

    container(name: 'maven') {
        git 'https://github.com/redhat-ipaas/ipaas-system-tests.git'

        def mavenOptions = namespace.isEmpty() ? "" : "-Dnamespace.use.existing=${namespace}"

        sh """
        mkdir -p \${HOME}/bin
        export PATH=\${PATH}:\${HOME}/bin
        mvn clean install -U ${mavenOptions}
        """
    }
}

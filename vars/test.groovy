#!/usr/bin/groovy

/**
 * Runs the system test suite.
 * @param parameters
 * @return
 */
def call(Map parameters = [:]) {

    def namespace = parameters.get('namespace', '')

    shareBinary('openshift', 'oc')

    container(name: 'maven') {
        git 'https://github.com/syndesisio/syndesis-system-tests.git'
        def mavenOptions = namespace.isEmpty() ? "" : "-Dnamespace.use.existing=${namespace}"

        //TODO: Fix usingLocalBinaries as withEnv isn't currently supported. Then use it instead of this:
        sh """
        mkdir -p \${HOME}/bin
        export PATH=\${PATH}:\${HOME}/bin
        mvn clean install -U ${mavenOptions}
        """
    }
}

#!/usr/bin/groovy

/**
 * Runs the system test suite.
 * @param parameters
 * @return
 */
def call(Map parameters = [:]) {

    def namespace = parameters.get('namespace', '')
    def envInitEnabled = parameters.get('envInitEnabled', true)

    shareBinary('openshift', 'oc')

    container(name: 'maven') {
        git 'https://github.com/syndesisio/syndesis-system-tests.git'
        def namespaceOption = namespace.isEmpty() ? "" : "-Dnamespace.use.existing=${namespace} -Denv.init.enabled=${envInitEnabled}"
        def mavenOptions = "${namespaceOption} -Dnamespace.destroy.enabled=true"

        //TODO: Fix usingLocalBinaries as withEnv isn't currently supported. Then use it instead of this:
        sh """
        env
        mkdir -p \${HOME}/bin
        export PATH=\${PATH}:\${HOME}/bin
        mvn clean install -U ${mavenOptions}
        """
    }
}

#!/usr/bin/groovy

/**
 * Runs the system test suite.
 * @param parameters
 * @return
 */
def call(Map parameters = [:]) {

    def namespace = parameters.get('namespace', '')
    def envInitEnabled = parameters.get('envInitEnabled', true)
    def namespaceDestroyEnabled = parameters.get('namespaceDestroyEnabled', true)

    shareBinary('openshift', 'oc')

    container(name: 'maven') {
        git 'https://github.com/syndesisio/syndesis-system-tests.git'
        def mavenOptions = "-Denv.init.enabled=${envInitEnabled} -Dnamespace.destroy.enabled=${namespaceDestroyEnabled}"
        if (!namespace.isEmpty()) {
           mavenOptions += " -Dnamespace.use.existing=${namespace}"
        }
        //TODO: Fix usingLocalBinaries as withEnv isn't currently supported. Then use it instead of this:
        sh """
        env
        mkdir -p \${HOME}/bin
        export PATH=\${PATH}:\${HOME}/bin
        mvn clean install -B -U ${mavenOptions}
        """
    }
}

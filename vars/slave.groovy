#!/usr/bin/groovy

/**
 * Wraps the code in a podTemplate with the JNLP container.
 * @param parameters    Parameters to customize the JNLP container.
 * @param body          The code to wrap.
 * @return
 */
def call(Map parameters = [:], body) {

    def defaultLabel = buildId('jnlp')
    def label = parameters.get('label', defaultLabel)

    def jnlpImage = parameters.get('jnlpImage', 'openshift/jenkins-slave-maven-centos7')
    def inheritFrom = parameters.get('inheritFrom', 'base')
    def serviceAccount = parameters.get('serviceAccount', '')

    podTemplate(label: label, inheritFrom: "${inheritFrom}", serviceAccount: "${serviceAccount}",
            containers: [containerTemplate(name: 'jnlp', image: "${jnlpImage}", args: '${computer.jnlpmac} ${computer.name}')]) {
        body()
    }
}


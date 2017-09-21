#!/usr/bin/groovy

/**
 * Wraps the code in a podTemplate with the Openshift container.
 * @param parameters    Parameters to customize the Openshift container.
 * @param body          The code to wrap.
 * @return
 */
def call(Map parameters = [:], body) {


    def defaultLabel = buildId('openshift')
    def label = parameters.get('label', defaultLabel)
    def name = parameters.get('name', 'openshift')

    def cloud = parameters.get('cloud', 'openshift')
    def openshiftImage = parameters.get('openshiftImage', 'openshift/origin:v3.6.0')
    def envVars = parameters.get('envVars', [])
    def inheritFrom = parameters.get('inheritFrom', 'base')
    def namespace = parameters.get('namespace', 'syndesis-ci')
    def serviceAccount = parameters.get('serviceAccount', '')
    def idleMinutes = parameters.get('idle', 10)

    podTemplate(cloud: "${cloud}", name: "${name}", namespace: "${namespace}", label: label, inheritFrom: "${inheritFrom}", serviceAccount: "${serviceAccount}",
            idleMinutesStr: "${idleMinutes}",
            containers: [containerTemplate(name: 'openshift', image: "${openshiftImage}", command: '/bin/sh -c', args: 'cat', ttyEnabled: true, envVars: envVars)]) {
        body()
    }
}


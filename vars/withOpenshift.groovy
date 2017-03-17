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

    def cloud = parameters.get('cloud', 'openshift')
    def openshiftImage = parameters.get('openshiftImage', 'openshift/origin:v1.5.0-rc.0')
    def inheritFrom = parameters.get('inheritFrom', 'base')
    def serviceAccount = parameters.get('serviceAccount', '')

    podTemplate(cloud: "${cloud}", label: label, inheritFrom: "${inheritFrom}", serviceAccount: "${serviceAccount}",
            containers: [containerTemplate(name: 'openshift', image: "${openshiftImage}", command: '/bin/sh -c', args: 'cat', ttyEnabled: true)]) {
        body()
    }
}


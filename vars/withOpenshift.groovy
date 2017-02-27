#!/usr/bin/groovy

def call(Map parameters = [:], body) {

    def defaultLabel = buildId(prefix : 'openshift')
    def label = parameters.get('label', defaultLabel)

    def openshiftImage = parameters.get('openshiftImage', 'openshift/origin:v1.4.1')
    def inheritFrom = parameters.get('inheritFrom', 'base')
    def serviceAccount = parameters.get('serviceAccount', '')

    podTemplate(label: label, inheritFrom: "${inheritFrom}", serviceAccount: "${serviceAccount}",
            containers: [containerTemplate(name: 'openshift', image: "${openshiftImage}", command: '/bin/sh -c', args: 'cat', ttyEnabled: true)]) {
        body()
    }
}

